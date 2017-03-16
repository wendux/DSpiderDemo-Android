/**
 * Created by du on 16/11/21.
 */

dSpider("jianshu", function(session,env,$){
    session.showProgress();
    var $items=$("div.title");
    var count=$items.length;
    session.setProgressMax(count)
    session.setProgressMsg("正在初始化");
    var i=0;
    var timer=setInterval(function(){
      session.setProgress(i+1);
      var title=$items.eq(i).text()
      session.setProgressMsg(title);
      session.push({title:title, url:$items.eq(i).parent().attr("href")})
      if(++i==count){
       clearInterval(timer);
       session.finish();
      }
    },200);
})
