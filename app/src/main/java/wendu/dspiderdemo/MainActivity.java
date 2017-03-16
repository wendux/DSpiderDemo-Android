package wendu.dspiderdemo;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.List;

import wendu.spidersdk.DSpider;
import wendu.spidersdk.DSpiderView;
import wendu.spidersdk.SpiderEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button visibleBtn;
    private Button silentBtn;
    private Button debugBtn;
    private Button logBtn;
    private Button resultBtn;
    private DSpiderView spiderView;
    private final int SID=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        visibleBtn =getView(R.id.visible);
        silentBtn =getView(R.id.silent);
        debugBtn =getView(R.id.debug);
        logBtn =getView(R.id.log);
        resultBtn=getView(R.id.result);
        spiderView=getView(R.id.spider_view);
        visibleBtn.setOnClickListener(this);
        silentBtn.setOnClickListener(this);
        debugBtn.setOnClickListener(this);
        logBtn.setOnClickListener(this);
        resultBtn.setOnClickListener(this);
    }

    public <T extends View> T getView(int resId){
            return (T) this.findViewById(resId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.visible:crawlVisible();break;
            case R.id.silent:crawlSilent();break;
            case R.id.debug:startDebug();break;
            case R.id.log:showLog();break;
            case R.id.result:showLastResult();break;
        }

    }

    private void crawlVisible(){
        DSpider.build(this).start(SID,"测试");
    }

    private void crawlSilent(){
       final ProgressDialog pd= ProgressDialog.show(this,"提示","正在爬取,请耐心等待",true,false);
        spiderView.start(SID, new SpiderEventListener() {
            @Override
            public void onResult(String sessionKey, List<String> data) {
                showDialog("爬取成功");
                pd.hide();
            }

            @Override
            public void onError(int code, String msg) {
                //如果错误，先判断是否可以重试
                if(spiderView.canRetry()){
                    Dialog alertDialog = new AlertDialog.Builder(MainActivity.this).
                            setTitle("提示").
                            setMessage("检测到新的爬取方案，是否重试？")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    spiderView.retry();
                                }
                            })
                            .create();
                    alertDialog.show();
                }else {
                    showDialog("失败了",msg);
                    pd.hide();
                }
            }
        });
    }

    //加载assets/jianshu.js
    private void startDebug(){
        DSpider.build(this).startDebug("简书","jianshu.js","http://www.jianshu.com/");
    }

    private void showLog(){
        String log=DSpider.getLastLog(this);
        if(TextUtils.isEmpty(log)){
            showDialog("日志","暂无日志");
        }else {
            showDialog(DSpider.getLastLog(this));
        }
    }

    private void showLastResult(){
        DSpider.Result result=DSpider.getLastResult(this);
        if(result!=null) {
            showDialog("爬取结果",result.datas.toString());
        }else {
            showDialog("暂无爬取结果");
        }
    }

    private void showDialog(String msg){
        showDialog("提示",msg);
    }
    private void showDialog(String title,String msg){
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle(title).
                setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
