package com.imooc.jigsaw;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageView[][] iv_game_arr = new ImageView[3][5];// 利用二维数组创建若干个游戏小方块

    private GridLayout gl_main_game; // 游戏主界面

    private ImageView iv_null_imageView;  // 空方块

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bitmap bigBm = ((BitmapDrawable) getResources().getDrawable(R.drawable.game_main)).getBitmap(); // 获取一张大的图
        int bigPicW = bigBm.getWidth() / 5; // 每个小方块的宽高

        // 初始化游戏的若干个小方块
        for (int i = 0; i < iv_game_arr.length; i++){
            for (int j = 0; j < iv_game_arr[0].length; j++){
                Bitmap bm = bigBm.createBitmap(bigBm, j * bigPicW, i * bigPicW, bigPicW, bigPicW);  // 根据行和列来切成若干个小图片

                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);  // 设置每一个游戏小方块的图案
                iv_game_arr[i][j].setPadding(2, 2, 2, 2);  // 设置方块之间的间距
                iv_game_arr[i][j].setTag(new GameData(i,j,bm));  // 绑定自定义的数据
                iv_game_arr[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = isHasByNullImageView((ImageView)v);

//                        Toast.makeText(MainActivity.this, "当前关系为" + flag, Toast.LENGTH_SHORT).show();

                        if(flag){
                            changeDataByImageView((ImageView) v);
                        }
                    }
                });
            }
        }

        gl_main_game = (GridLayout) findViewById(R.id.gl_main_game); // 初始化界面 添加若干小方块

        for (int i = 0; i < iv_game_arr.length; i++){
            for (int j = 0; j < iv_game_arr[0].length; j++){
                gl_main_game.addView(iv_game_arr[i][j]);
            }
        }

        setNullImageView(iv_game_arr[2][4]);
    }

    /**
     *  利用动画结束之后，交换两个方块的数据
     *  @param mImageView 点击的方块
     *
     */
    public void changeDataByImageView(final ImageView mImageView){
        // 创建动画，设置好方向，移动的距离
        TranslateAnimation translateAnimation = null;

        if(mImageView.getX() > iv_null_imageView.getX()){
            translateAnimation = new TranslateAnimation(0.1f, -mImageView.getWidth(), 0.1f, 0.1f); // 往上移
        }else if(mImageView.getX() < iv_null_imageView.getX()){
            translateAnimation = new TranslateAnimation(0.1f, mImageView.getWidth(), 0.1f, 0.1f); // 往下移
        }else if(mImageView.getY() > iv_null_imageView.getY()){
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, -mImageView.getWidth()); // 往左移
        }else if(mImageView.getY() < iv_null_imageView.getY()){
            translateAnimation = new TranslateAnimation(0.1f, 0.1f, 0.1f, mImageView.getWidth()); // 往右移
        }
        // 设置动画的时长
        translateAnimation.setDuration(70);
        // 设置动画结束后是否停留
        translateAnimation.setFillAfter(true);
        // 设置动画结束之后要真正的把数据交换
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageView.clearAnimation();
                GameData mGameData = (GameData)mImageView.getTag();
                iv_null_imageView.setImageBitmap(mGameData.bm);

                GameData mNullGameData = (GameData) iv_null_imageView.getTag();
                mNullGameData.bm = mGameData.bm;
                mNullGameData.p_x = mGameData.p_x;
                mNullGameData.p_y = mGameData.p_y;

                setNullImageView(mImageView); //设置当前点为空方块
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //
        mImageView.startAnimation(translateAnimation);
    }

    /**
     *  设置某个方块为空方块
     * @param mImageView 当前要设置为空的方块
     */
    public void setNullImageView(ImageView mImageView){
        mImageView.setImageBitmap(null);
        iv_null_imageView = mImageView;
    }

    /**
     *  判断当前点击的方块,是否与空方块是相邻关系
     *  @param mImageView 所点击的方块
     *  @return true 相邻， false 不相邻
     */
    public boolean isHasByNullImageView(ImageView mImageView){
        // 分别获取当前空方块的位置与点击方块的位置 通过x,y 差1
        GameData mNullGameData = (GameData) iv_null_imageView.getTag();
        GameData mGameData = (GameData) mImageView.getTag();

        if(mGameData.y == mNullGameData.y && mGameData.x + 1 == mNullGameData.x){ // 当前点击的方块在空方块的上方
            return true;
        }else if (mGameData.y == mNullGameData.y && mGameData.x - 1 == mNullGameData.x){ // 当前点击的方块在空方块的下方
            return true;
        }else if (mGameData.y + 1 == mNullGameData.y && mGameData.x == mNullGameData.x){ // 当前点击的方块在空方块的左方
            return true;
        }else if (mGameData.y - 1 == mNullGameData.y && mGameData.x == mNullGameData.x){ // 当前点击的方块在空方块的右方
            return true;
        }
        return false;
    }

    /**
     * 每个游戏小方块上要绑定的数据
     */
    class GameData {
        public int x = 0;    // 每个小方块的实际位置x
        public int y = 0;    // 每个小方块的实际位置y
        public Bitmap bm;    // 每个小方块的图片
        public int p_x = 0;    // 每个小方块的图片位置x
        public int p_y = 0;    // 每个小方块的图片位置y

        public GameData(int x, int y, Bitmap bm) {
            super();
            this.x = x;
            this.y = y;
            this.bm = bm;
            this.p_x = x;
            this.p_y = y;
        }
    }
}
