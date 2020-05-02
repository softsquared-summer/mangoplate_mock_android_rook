package com.example.mangoplate.src.main;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mangoplate.R;
import com.example.mangoplate.src.BaseActivity;
import com.example.mangoplate.src.main.interfaces.MainActivityView;
import com.facebook.login.LoginManager;
import com.kakao.auth.AuthType;
import com.kakao.auth.Session;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
public class MainActivity extends BaseActivity implements MainActivityView { // 여기서는 뷰만 컨트롤
    private TextView mTvHelloWorld;
    private ImageView mbtnFacebookLogin;
    private FacebookSessionCallback mLoginCallback;
    private CallbackManager mCallbackManager;
    private String mMainFacebookJsonString;
    private KakaotalkSessionCallback kakaoSessionCallback = new KakaotalkSessionCallback(this);
    Session session;
    private ImageView mbtnKakaoLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getHashKey();//VbKsFknDcqZ3CHUR47Mlsw2cGOU=
        // mainApplication.java
        FacebookSdk.sdkInitialize(getApplicationContext());// 페이스북은 이렇게 카카오톡은 Application.class 수정해야함.
        setContentView(R.layout.activity_main);
        mbtnFacebookLogin = (ImageView) findViewById(R.id.main_facebookbutton);
        ImageView main_menu = (ImageView) findViewById(R.id.main_menu);
        AppEventsLogger.activateApp(this);
        CallbackManager callbackManager;
        callbackManager = CallbackManager.Factory.create();
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(main_menu);
        Glide.with(this).load(R.drawable.mangoplate).diskCacheStrategy(DiskCacheStrategy.SOURCE).placeholder(R.drawable.mangoplate)
               .into(gifImage);
//        출처: https://gogorchg.tistory.com/entry/Android-Glide-에서-Gif-로드가-너무-느려요 [항상 초심으로]
        mbtnKakaoLogin =  findViewById(R.id.main_kakaobutton);
        mbtnKakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = Session.getCurrentSession();
                showProgressDialog();
                session.addCallback(kakaoSessionCallback);
                session.open(AuthType.KAKAO_LOGIN_ALL, MainActivity.this);
            }
        });

        mbtnFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager loginManager = LoginManager.getInstance();
                loginManager.logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
                AccessToken token = AccessToken.getCurrentAccessToken();
                mMainFacebookJsonString="{\"at\" : \""+token.getToken()+"\"}";
                Log.e("보낼려는 토큰",""+mMainFacebookJsonString);
                main_tryPost("facebook",mMainFacebookJsonString);
                if (token != null) {
                    Log.e("토큰", "Token: " + token.getToken());
                    Log.e("유저아이디.", "UserID: " + token.getUserId());
                }
                loginManager.registerCallback(mCallbackManager, mLoginCallback);
            }
        });
        mCallbackManager = CallbackManager.Factory.create();
        mLoginCallback = new FacebookSessionCallback(this);




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

    }
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
    private void main_tryPost(String type, String jsonString) {
              showProgressDialog();

        final MainService mainService = new MainService(this);
//        mainService.getTest();
        mainService.tryPost(type,jsonString);
    }

    @Override
    public void validateSuccess(String text) {
        hideProgressDialog();
        mTvHelloWorld.setText(text);
    }

    @Override
    public void validateFailure(@Nullable String message) {
        hideProgressDialog();
        showCustomToast(message == null || message.isEmpty() ? getString(R.string.network_error) : message);
    }

    public void customOnClick(View view) {
        switch (view.getId()) {


//            case main_facebookbutton:
//                break;
//            case R.id.main_btn_hello_world:
//                tryGetTest();
//                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 세션 콜백 삭제
        com.kakao.auth.Session.getCurrentSession().removeCallback(kakaoSessionCallback);
    }
}
