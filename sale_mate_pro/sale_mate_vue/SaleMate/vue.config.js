module.exports={
  //  输出目录
    outputDir:'./dist',
    // 静态资源目录
    assetsDir:'assets',
    // 生成的index.html的输出路径
    indexPath:'index.html',
    // 生成的静态资源文件名包含hash
    filenameHashing:true,
  devServer:{
      host:'0.0.0.0',
      port:8081,
      hot: true,
      open:true
  }
}