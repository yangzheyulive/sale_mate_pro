package com.salemate.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.salemate.bo.CustomerBo;
import com.salemate.common.util.WxApiUtil;
import com.salemate.common.util.WxConstantKeys;
import com.salemate.mapper.*;
import com.salemate.model.*;
import com.salemate.service.CustomerService;
import com.salemate.service.PayBillService;
import com.salemate.vo.CustomerSyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private PayBillService payBillService;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private CustomerTagMapper customerTagMapper;

    // 用户同步锁
    private static final TimedCache<String, CustomerSyncVo> syncLock = CacheUtil.newTimedCache(1000 * 60 * 300);
    private static final TimedCache<String, Integer> syncCountCache = CacheUtil.newTimedCache(1000 * 60);


    @Override
    public int updateBatch(List<Customer> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int batchInsert(List<Customer> list) {
        return baseMapper.batchInsert(list);
    }

    /**
     * 同步企业标签功能
     *
     * @param corpId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerSyncVo syncCustomer(String corpId) {
        try {
            CustomerSyncVo customerSyncVo = syncLock.get(corpId);
            if (customerSyncVo == null) {
                customerSyncVo = new CustomerSyncVo();
                customerSyncVo.setStatus(1);
                customerSyncVo.setCount(0L);
                syncLock.put(corpId, customerSyncVo);

            } else {
                return customerSyncVo;
            }
            String suiteId = WxApiUtil.getWxProperties().getSuiteId();
            Enterprise enterprise = enterpriseMapper.selectOne(new QueryWrapper<Enterprise>().eq("corp_id", corpId).eq("suite_id", suiteId));
            log.info(">>> 企业信息:{}", enterprise);
            // 如果不存在获取通讯录的秘钥停止同步
            if (!StringUtils.hasText(enterprise.getUserSecret())) {
                log.info(">>> 获取通讯录的秘钥不存在停止同步");
                customerSyncVo.setStatus(-1);
                customerSyncVo.setMsg("获取通讯录的秘钥不存在停止同步");
                throw new RuntimeException("获取通讯录的秘钥不存在停止同步");
            }
            String corpToken = WxApiUtil.getCorpToken(corpId, enterprise.getPermanentCode());
            // 获取的是通讯录的access_token
            String accessToken = WxApiUtil.getUserAccessToken(enterprise.getCorpId(), enterprise.getUserSecret());

            if (accessToken == null) {
                customerSyncVo.setStatus(-1);
                customerSyncVo.setMsg("获取通讯录的秘钥失败");
                throw new RuntimeException("获取通讯录的秘钥不存在停止同步");
            }
            // 获取成员列表
            JSONArray userIds = WxApiUtil.getUserIds(accessToken);
            List<JSONObject> userInfoList = new ArrayList<>();
            for (int i = 0; i < userIds.size(); i++) {
                String userid = userIds.getJSONObject(i).getString("userid");
                try {
                    JSONObject userInfo = WxApiUtil.getUserInfo(corpToken, userid);
                    userInfoList.add(userInfo);
                    log.info(">>> userInfo:{}", userInfo);

                } catch (Exception e) {
                    log.error(">>> 获取用户信息失败:{}", userid);
                }

            }
            log.info(">>> userInfoList:{}", userInfoList);
            // 批量获取
            readCustomer(corpId, userInfoList, null);
            //总数
            Long count = syncLock.get(corpId).getCount();
            // 已经同步的数量
            syncCountCache.put(corpId, count.intValue());
            //释放锁
            syncLock.remove(corpId);
            CustomerSyncVo vo = new CustomerSyncVo();
            vo.setMsg("同步成功");
            vo.setStatus(2);
            return vo;
        } catch (Exception e) {
            log.error(">>> 同步失败:{}", e);
            syncLock.remove(corpId);
            syncCountCache.put(corpId, -1);
            CustomerSyncVo vo = new CustomerSyncVo();
            vo.setMsg("同步失败");
            vo.setStatus(-1);
            return vo;
        } finally {

        }
    }



    private int readCustomer(String corpId, List<JSONObject> userInfoList, String cursor) {
        String customerAccessToken = WxApiUtil.getCustomerAccessToken(corpId, "hXbUxPms6SCTxbgzB34Fmhy8KUQ67-Qy6PapR1znI8Y");
        List<String> userIdList = CollUtil.getFieldValues(userInfoList, "userid", String.class);

        JSONObject externalContactDetail1 = WxApiUtil.getExternalContactDetail(userIdList, customerAccessToken, cursor);
        JSONArray dataList = externalContactDetail1.getJSONArray("external_contact_list");
        String next_cursor = externalContactDetail1.getString("next_cursor");
        List<Customer> customerList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {

            JSONObject data = dataList.getJSONObject(i);
            JSONObject external_contact = data.getJSONObject("external_contact");
            JSONObject follow_info = data.getJSONObject("follow_info");
            Customer customer = new Customer();
            customer.setCorpId(corpId);
            customer.setAvatar(external_contact.getString("avatar"));
            customer.setExternalUserid(external_contact.getString("external_userid"));
            customer.setName(external_contact.getString("name"));
            customer.setGender(external_contact.getInteger("gender"));
            customer.setType(external_contact.getInteger("type"));
            JSONArray tag_id = follow_info.getJSONArray("tag_id");
            if (CollUtil.isNotEmpty(tag_id)) {
                JSONObject tagCorpList = WxApiUtil.getTagCorpList(customerAccessToken, tag_id);
                log.info(">>>标签同步 tagCorpList:{}", tagCorpList);
                JSONArray tag_group = tagCorpList.getJSONArray("tag_group");
                StringBuilder tagName = new StringBuilder();
                for (int i1 = 0; i1 < tag_group.size(); i1++) {
                    JSONObject tagGroupItem = tag_group.getJSONObject(i1);
                    JSONArray taglist = tagGroupItem.getJSONArray("tag");
                    for (int i2 = 0; i2 < taglist.size(); i2++) {
                        JSONObject tagItem = taglist.getJSONObject(i2);
                        String id = tagItem.getString("id");
                        String name = tagItem.getString("name");
                        tagName.append(name + "/");
                        CustomerTag customerTag = new CustomerTag();
                        customerTag.setCustomerId(external_contact.getString("external_userid"));
                        customerTag.setTagId(id);
                        Long count = customerTagMapper.selectCount(new QueryWrapper<>(customerTag));
                        if (count == 0) {
                            customerTagMapper.insert(customerTag);
                        }
                    }
                }
                customer.setTagName(tagName.toString());
            }

            customer.setRemark(follow_info.getString("remark"));
            customer.setDescription(follow_info.getString("description"));
            customer.setAddWay(follow_info.getInteger("add_way"));
            customer.setOperUserid(follow_info.getString("oper_userid"));
            for (int i1 = 0; i1 < userInfoList.size(); i1++) {
                JSONObject jsonObject = userInfoList.get(i1);
                if (jsonObject.getString("userid").equals(follow_info.getString("userid"))) {
                    log.info(">>> 通讯录成员:{}", jsonObject);
                    customer.setUserName(jsonObject.getString("name"));
                    customer.setUserId(follow_info.getString("userid"));
                }
            }
            //时间戳转换 Date类型
            String createtime = follow_info.getString("createtime");
            log.info("customer >>>>>:{}", customer.toString());
            if (StringUtils.hasText(createtime)) {
                customer.setCreateDate(new Date(Long.parseLong(createtime) * 1000));
            }
            customerList.add(customer);
            boolean b = saveOrUpdate(customer);
            if (b) {
                Long count = syncLock.get(corpId).getCount();
                syncLock.get(corpId).setCount(count + 1);
            }


        }
        int count = 0;
        if (StringUtils.hasText(next_cursor)) {
            count =readCustomer(corpId, userInfoList, next_cursor);
        }
        log.info(">>> 同步数量:{}",  customerList.size() + count);

        return customerList.size() + count;
    }


    @Override
    public void syncCustomerBillTag(String corpId, String startTime, String endTime) {
        //企业是否有创建标签
        Enterprise enterprise = enterpriseMapper.selectOne(new QueryWrapper<Enterprise>().eq("corp_id",corpId).eq("suite_id",WxApiUtil.getWxProperties().getSuiteId()));
        String customerAccessToken = WxApiUtil.getCustomerAccessToken(corpId, enterprise.getCustomerSecret());
        String corpToken = WxApiUtil.getCorpToken(corpId, enterprise.getPermanentCode());
        //读取标签
        JSONObject tagCorpList = WxApiUtil.getTagCorpList(customerAccessToken, null);
        //标签组
        JSONArray tag_group = tagCorpList.getJSONArray("tag_group");
        List<JSONObject> tagList = null;
        JSONObject tagGroup = null;
        for (int i = 0; i < tag_group.size(); i++) {
            //保存所有标签组
            JSONObject group = tag_group.getJSONObject(i);
            String group_name = group.getString("group_name");
            String group_id = group.getString("group_id");
            Date create_time = group.getDate("create_time");
            Group groupEntity = new Group();
            groupEntity.setGroupId(group_id);
            groupEntity.setGroupName(group_name);
            groupEntity.setCreateDate(create_time);
            groupEntity.setCorpId(corpId);
            //保存标签组
            Group group1 = groupMapper.selectById(group_id);
            if (group1 == null) {
                groupMapper.insert(groupEntity);
            } else {
                groupMapper.updateById(groupEntity);
            }
            //保存标签
            List<JSONObject> tagJsonList = tag_group.getJSONObject(i)
                    .getJSONArray("tag")
                    .toJavaList(JSONObject.class);
            for (JSONObject jsonObject : tagJsonList) {
                String tag_id = jsonObject.getString("id");
                String tag_name = jsonObject.getString("name");
                Date create_time1 = jsonObject.getDate("create_time");
                Tag tag = new Tag();
                tag.setTagId(tag_id);
                tag.setName(tag_name);

                tag.setGroupId(group_id);
                tag.setCreateDate(create_time1);
                Tag tag1 = tagMapper.selectById(tag_id);
                if (tag1 == null) {
                    tagMapper.insert(tag);
                } else {
                    tagMapper.updateById(tag);
                }
            }
            //标签检测账单标签是否存在
            if ("SALEMATE标签组".equals(group_name)) {
                //获取标签
                tagGroup = tag_group.getJSONObject(i);
                tagList = tag_group.getJSONObject(i)
                        .getJSONArray("tag")
                        .toJavaList(JSONObject.class);
            }
        }
        //创建标签
        if (tagGroup == null) {
            //创建标签组
            tagGroup = new JSONObject();
            tagGroup.put("group_name", "SALEMATE标签组");

        }
        //创建标签
        if (CollUtil.isEmpty(tagList)) {
            tagList = new ArrayList<>();
            //创建标签组
            JSONObject tagItem1 = new JSONObject();
            tagItem1.put("name", "未购买");
            JSONObject tagItem2 = new JSONObject();
            tagItem2.put("name", "促复购");
            JSONObject tagItem3 = new JSONObject();
            tagItem3.put("name", "转介绍");
            tagList.add(tagItem1);
            tagList.add(tagItem2);
            tagList.add(tagItem3);
            JSONObject jsonObject = WxApiUtil.addCorpTag(customerAccessToken, tagGroup, tagList);
            log.info(">>> 创建标签:{}", jsonObject);
            tagList = jsonObject.getJSONArray("tag").toJavaList(JSONObject.class);
        } else {
            //
            List<JSONObject> newTagList = new ArrayList<>();
            //查询支付标签是否存在
            JSONObject notBuy = CollUtil.findOne(tagList, (data) -> data.getString("name").equals("未购买"));
            JSONObject buy = CollUtil.findOne(tagList, (data) -> data.getString("name").equals("促复购"));
            JSONObject zjs = CollUtil.findOne(tagList, (data) -> data.getString("name").equals("转介绍"));
            if (notBuy == null) {
                JSONObject tagItem1 = new JSONObject();
                tagItem1.put("name", "未购买");
                newTagList.add(tagItem1);
            }
            if (buy == null) {
                JSONObject tagItem2 = new JSONObject();
                tagItem2.put("name", "促复购");
                newTagList.add(tagItem2);
            }
            if (zjs == null) {
                JSONObject tagItem3 = new JSONObject();
                tagItem3.put("name", "转介绍");
                newTagList.add(tagItem3);
            }
            if (CollUtil.isNotEmpty(newTagList)) {
                JSONObject jsonObject = WxApiUtil.addCorpTag(customerAccessToken, tagGroup, newTagList);
                log.info(">>> 补充标签:{}", jsonObject);
                tagList = jsonObject.getJSONArray("tag").toJavaList(JSONObject.class);
            }
        }
        log.info(">>> 标签列表:{}", tagList);

        //获取几种类型的标签
        JSONObject not_buy = CollUtil.findOne(tagList, (data) -> data.getString("name").equals("未购买"));
        JSONObject buy = CollUtil.findOne(tagList, (data) -> data.getString("name").equals("促复购"));
        JSONObject zjs = CollUtil.findOne(tagList, (data) -> data.getString("name").equals("转介绍"));
        //同步订单标签
        String payToken = WxApiUtil.getCorpPayAccessToken(corpId, enterprise.getBillSecret());
        List<JSONObject> bill_list = syncBillOrderList(payToken, corpId, startTime, endTime, null );
        List<PayBill> payBills = BeanUtil.copyToList(bill_list, PayBill.class);
        payBillService.saveOrUpdateBatch(payBills);
        //过滤收款记录
        bill_list = CollUtil.filter(bill_list, (data) -> data.getInteger("bill_type") == 0);
        List<String> external_userid1 = CollUtil.getFieldValues(bill_list, "external_userid", String.class);
        if (CollUtil.isEmpty(external_userid1)) {
            throw new RuntimeException("没有收款记录");
        }
        //获取存在的用户
        List<Customer> list = lambdaQuery().eq(Customer::getCorpId, corpId)
                .in(Customer::getExternalUserid, external_userid1)
                .list();

        log.info(">>> 收款记录:{}", bill_list);
        for (Customer customer : list) {
        for (int i = 0; i < bill_list.size(); i++) {
                //收款记录
                JSONObject billItem = bill_list.get(i);
                //获取收款记录并且已经完成的用户收款记录
                String external_userid = billItem.getString("external_userid");
                if (external_userid.equals(customer.getExternalUserid())) {
                    List<PayBill> payBills1 = payBillService.lambdaQuery()
                            .eq(PayBill::getExternalUserid, external_userid)
                            .list();
                    //促复购
                    if ( payBills1.size() > 0) {
                        customer.setLabelType(WxConstantKeys.LabelType.Buy.getValue());
                        List<String> tagIdList = new ArrayList<>();
                        tagIdList.add(buy.getString("id"));
                        List<String> removeIds = new ArrayList<>();
                        removeIds.add(not_buy.getString("id"));
                        removeIds.add(zjs.getString("id"));

                        JSONObject res = WxApiUtil.markTag(customer.getExternalUserid(), customer.getUserId(), tagIdList,removeIds, corpToken);
                        log.info(">>> 标签打标结果:{}", res);
                        updateById(customer);
                    }
                    //转介绍
                    if ( payBills1.size() > 1) {
                        customer.setLabelType(WxConstantKeys.LabelType.INTRODUCE.getValue());
                        List<String> tagIdList = new ArrayList<>();
                        tagIdList.add(zjs.getString("id"));
                        List<String> removeIds = new ArrayList<>();
                        removeIds.add(not_buy.getString("id"));
                        removeIds.add(buy.getString("id"));

                        JSONObject res = WxApiUtil.markTag(customer.getExternalUserid(), customer.getUserId(), tagIdList, removeIds, corpToken);
                        log.info(">>> 标签打标结果:{}", res);
                        updateById(customer);
                    }
                }
                //未购买
                if (customer.getLabelType() == WxConstantKeys.LabelType.NO_Buy.getValue()) {
                    List<String> tagIdList = new ArrayList<>();
                    tagIdList.add(not_buy.getString("id"));
                    List<String> removeIds = new ArrayList<>();
                    removeIds.add(buy.getString("id"));
                    removeIds.add(zjs.getString("id"));
                    JSONObject res = WxApiUtil.markTag(customer.getExternalUserid(), customer.getUserId(), tagIdList, removeIds, corpToken);
                    log.info(">>> 标签打标结果:{}", res);
                }
            }
        }
    }
    private List<JSONObject> syncBillOrderList( String accessToken,String corpId, String startTime, String endTime, String cursor) {
        long start = DateUtil.parse(startTime, "yyyy-MM-dd").getTime() / 1000;
        long end = DateUtil.parse(endTime, "yyyy-MM-dd").getTime() / 1000;

        JSONObject billres = WxApiUtil.getBillList(accessToken, start, end, cursor);
        JSONArray bill_list = billres.getJSONArray("bill_list");
        log.info(">>> 收款记录:{}", bill_list);
        String next_cursor = billres.getString("next_cursor");
        if (StringUtils.hasText(next_cursor)) {
            bill_list.addAll( syncBillOrderList(accessToken,corpId, startTime, endTime, next_cursor));
            return bill_list.toJavaList(JSONObject.class);
        }
        return bill_list.toJavaList(JSONObject.class);
    }

    @Override
    public CustomerSyncVo getSyncCustomerStatus(String corpId) {
        Long total = baseMapper.selectCount(Wrappers.<Customer>lambdaQuery().eq(Customer::getCorpId, corpId));

        CustomerSyncVo customerSyncVo = syncLock.get(corpId);
        if (customerSyncVo == null) {
            Integer integer = syncCountCache.get(corpId);
            if (integer == -1) {
                customerSyncVo = new CustomerSyncVo();
                customerSyncVo.setCorpId(corpId);
                customerSyncVo.setStatus(-1);
                customerSyncVo.setMsg("同步失败");
                syncCountCache.remove(corpId);
                return customerSyncVo;
            }
            customerSyncVo = new CustomerSyncVo();
            customerSyncVo.setCorpId(corpId);
            customerSyncVo.setCount(100L);
            customerSyncVo.setStatus(2);
//            syncCountCache.remove(corpId);
            return customerSyncVo;
        }else {
            //计算百分比
            Long count = customerSyncVo.getCount();

            BigDecimal divide = new BigDecimal(count).divide(new BigDecimal(total), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
            if (divide.longValue() > 100) {
                divide = new BigDecimal(99);
            }
            CustomerSyncVo customerSyncVo1 = new CustomerSyncVo();
            customerSyncVo1.setCorpId(corpId);
            customerSyncVo1.setCount(divide.longValue());
            customerSyncVo1.setStatus(1);
            return customerSyncVo1;
        }


    }


    @Override
    public Page<Customer> queryPage(CustomerBo bo) {
        Page page = new Page<Customer>(bo.getPageNum().intValue(),bo.getPageSize().intValue());
        return baseMapper.queryPage(page,bo);
    }
}


