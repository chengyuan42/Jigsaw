package com.imooc.jigsaw;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private boolean isAnimRun = false; // 动画是否正在执行
    private boolean isGameStarted = false; // 游戏是否开始
    private ImageView[][] iv_game_arr = new ImageView[3][5];// 利用二维数组创建若干个游戏小方块

    private GridLayout gl_main_game; // 游戏主界面

    private ImageView iv_null_imageView;  // 空方块

    private GestureDetector mDetector; // 当前手势

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                int type = getDirByGes(motionEvent.getX(), motionEvent.getY(), motionEvent1.getX(), motionEvent1.getY());
                changeByDir(type);
//                Toast.makeText(MainActivity.this, "" + type, Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        Bitmap bigBm = ((BitmapDrawable) getResources().getDrawable(R.drawable.game_main)).getBitmap(); // 获取一张大的图
        int bigPicW = bigBm.getWidth() / 5; // 每个小方块的宽高

        int ivWindH = getWindowManager().getDefaultDisplay().getWidth() / 5;

        // 初始化游戏的若干个小方块
        for (int i = 0; i < iv_game_arr.length; i++){
            for (int j = 0; j < iv_game_arr[0].length; j++){
                Bitmap bm = bigBm.createBitmap(bigBm, j * bigPicW, i * bigPicW, bigPicW, bigPicW);  // 根据行和列来切成若干个小图片

                iv_game_arr[i][j] = new ImageView(this);
                iv_game_arr[i][j].setImageBitmap(bm);  // 设置每一个游戏小方块的图案
                iv_game_arr[i][j].setLayoutParams(new RelativeLayout.LayoutParams(ivWindH, ivWindH));
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

        randomMove(); // 随机打乱方块顺序
    }

    /**
     * 根据手势的方向,获取空方块相应的相邻位置 如果存在方块,那么进行交换
     *
     * @param type 1:上 2:下 3:左 4:右
     */
    public void changeByDir(int type) {
        changeByDir(type, true);
    }


    /**
     * 根据手势的方向,获取空方块相应的相邻位置 如果存在方块,那么进行交换
     *
     * @param type   1:上 2:下 3:左 4:右
     * @param isAnim true 有动画, false 没有动画
     */
    public void changeByDir(int type, boolean isAnim) {
        // 当前空方块的位置
        GameData mNullGameData = (GameData) iv_null_imageView.getTag();
        int new_x = mNullGameData.x;
        int new_y = mNullGameData.y;

        if (type == 1) { // 要移动的方块在当前恐反恐的位置
            new_x++;
        } else if (type == 2) {
            new_x--;
        } else if (type == 3) {
            new_y++;
        } else if (type == 4) {
            new_y--;
        }

        // 判断这个新坐标,是否存在
        if (new_x >= 0 && new_x < iv_game_arr.length && new_y >= 0 && new_y < iv_game_arr[0].length) {
            if (isAnim) {
                changeDataByImageView(iv_game_arr[new_x][new_y]);
            } else {
                changeDataByImageView(iv_game_arr[new_x][new_y], isAnim);
            }
        } else {
            // 不动
        }
    }

    public void gameOver() {
        boolean isGameOver = true;
        // 要遍历每个游戏小方块
        for (int i = 0; i < iv_game_arr.length; i++) {
            for (int j = 0; j < iv_game_arr[0].length; j++) {
                // 为空的方块数据不判断跳过
                if (iv_game_arr[i][j] == iv_null_imageView) {
                    continue;
                }
                GameData mGameData = (GameData) iv_game_arr[i][j].getTag();
                if (!mGameData.isTrue()) {
                    isGameOver = false;
                    break;
                }
            }
        }

        if (isGameOver) {
            Toast.makeText(this, "游戏结束", Toast.LENGTH_LONG).show();
        }
        // 为空的方块数据
    }


    /**
     * 判断手势,是向右滑,向左滑
     *
     * @param start_x 手势的起始点x
     * @param start_y 手势的起始点y
     * @param end_x   手势的终止点x
     * @param end_y   手势的终止点x
     * @return 1:上 2:下 3:左 4:右
     */
    public int getDirByGes(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = (Math.abs(start_x - end_x) > Math.abs(start_y - end_y)) ? true : false;

        if (isLeftOrRight) {  // 左右
            boolean isLeft = start_x - end_x > 0 ? true : false;
            if (isLeft) {
                return 3;
            } else {
                return 4;
            }
        } else {  // 上下
            boolean isUp = start_y - end_y > 0 ? true : false;
            if (isUp) {
                return 1;
            } else {
                return 2;
            }
        }
    }

    public void randomMove() {
        // 打乱的次数
        for (int i = 0; i < 100; i++) {
            int type = (int) (Math.random() * 4) + 1;
            changeByDir(type, false);
            isGameStarted = true;
        }
        // 根据手势开始交互,无动画
    }

    /**
     * 利用动画结束之后，交换两个方块的数据
     *
     * @param mImageView 点击的方块
     */
    public void changeDataByImageView(final ImageView mImageView) {
        changeDataByImageView(mImageView, true);
    }

    /**
     *  利用动画结束之后，交换两个方块的数据
     *  @param mImageView 点击的方块
     *
     */
    public void changeDataByImageView(final ImageView mImageView, boolean isAnim) {
        if (isAnimRun) {
            return;
        }

        if (!isAnim) {
            GameData mGameData = (GameData) mImageView.getTag();
            iv_null_imageView.setImageBitmap(mGameData.bm);

            GameData mNullGameData = (GameData) iv_null_imageView.getTag();
            mNullGameData.bm = mGameData.bm;
            mNullGameData.p_x = mGameData.p_x;
            mNullGameData.p_y = mGameData.p_y;

            setNullImageView(mImageView); //设置当前点为空方块
            if (isGameStarted) {
                gameOver();
            }
            return;
        }
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
        translateAnimation.setDuration(500);
        // 设置动画结束后是否停留
        translateAnimation.setFillAfter(true);
        // 设置动画结束之后要真正的把数据交换
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRun = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRun = false;
                mImageView.clearAnimation();
                GameData mGameData = (GameData)mImageView.getTag();
                iv_null_imageView.setImageBitmap(mGameData.bm);

                GameData mNullGameData = (GameData) iv_null_imageView.getTag();
                mNullGameData.bm = mGameData.bm;
                mNullGameData.p_x = mGameData.p_x;
                mNullGameData.p_y = mGameData.p_y;

                setNullImageView(mImageView); //设置当前点为空方块
                if (isGameStarted) {
                    gameOver();
                }
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

        public boolean isTrue() {
            return x == p_x && y == p_y;
        }
    }
}
