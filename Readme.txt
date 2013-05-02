# 许仙明的广发证劵笔试结果说明

1）文件目录说明
   doc        相关文档
   gen-java   由thrift生成
   gen-nodejs 由thrift生成
   img        html页面展示时所需图片
   MarketDataSimulator   股票行情模拟生成器的java代码
   node_modules          node.js相关的包
   stylesheets           html页面展示所需的css文件
   app.js
   index.html
   jquery.js                处理json时用到的js
   MarketDataSimulator.jar  股票模拟生成器导出的可执行文件
   package.json             包说明文件
   Readme.txt
   SendStockDataTool.thrift   thrift所需的接口说明文件

2）运行环境要求
   安装 jdk 1.6  
   安装 node.js  v0.10.5
   安装 npm  1.2.18

3）股票行情模拟生成器
   运行 java -jar MarketDataSimulator.jar
   此时在命令行可以看到股票行情信息输出
   
   本股票行情模拟生成器的行情生成规则在如下文件描述：
      doc文件夹下“股票生成器说明.bmp”，若该文件显示不清晰，请用visio 2007及以上版本软件打开“股票生成器说明.vsd”；
      
   由于本人在股票行情模拟生成器中，简化了部分股票交易规则；
   请务必在使用该模拟器前，阅读“股票生成器说明.bmp”；
   
   MarketDataSimulator.jar通过thrift机制向node.js传送数据，使用端口为 9799；
   因此请确保该端口可以使用；

4）股票行情web展示
   进入StockDataSimulator目录后，运行 npm install
   运行 node app.js
   在chrome浏览器中打开 http://localhost:8080/
   （注意前端仅测试过chrome，不保证其他浏览器可以正确运行）
   此时可以看到浏览器中展示的股票行情信息