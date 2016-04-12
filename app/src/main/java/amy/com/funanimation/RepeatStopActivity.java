package amy.com.funanimation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RepeatStopActivity extends AppCompatActivity {
    private boolean timeLeftAnimationStarted = false;

    int count = 100;
    private static final int START_ANIMATION_COUNT = 95;

    private AnimatorSet timeLeftanimatorSet;

    TextView countView;

    private ScheduledExecutorService executor;

    private ScheduledFuture<?> timeRefreshTask;
    private Handler handler = new Handler();
    private AnimatorListenerAdapter animatorListenerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_stop);

        countView = (TextView) findViewById(R.id.count_down);

        makeObject();
    }

    private void makeObject() {
        executor = Executors.newSingleThreadScheduledExecutor();
        handler = new Handler();

        ObjectAnimator animeFadeIn = ObjectAnimator.ofFloat(countView, "alpha", 0f, 1f);
        animeFadeIn.setDuration(800);
        ObjectAnimator animeFadeOut = ObjectAnimator.ofFloat(countView, "alpha", 1f, 0f);
        animeFadeOut.setDuration(400);

//        List<Animator> timeLeftAnimList = new ArrayList<>();
//        timeLeftAnimList.add(animeFadeOut);
//        timeLeftAnimList.add(animeFadeIn);

        timeLeftanimatorSet = new AnimatorSet();
        timeLeftanimatorSet.play(animeFadeIn).with(animeFadeOut).after(0);

        animatorListenerAdapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                timeLeftanimatorSet.start();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        };
//        timeLeftanimatorSet.playSequentially(timeLeftAnimList);


    }

    public void onComponentClicked(View view) {
        switch (view.getId()) {
            case R.id.start: {
                if (executor != null) {
                    if (timeRefreshTask == null || timeRefreshTask.isCancelled()) {
                        timeRefreshTask = executor.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        upDateCountView();
                                    }
                                });
                            }
                        }, 0, 1, TimeUnit.SECONDS);
                    }
                }
            }
            break;

            case R.id.stop: {
                timeRefreshTask.cancel(true);
                count = 100;
                upDateCountView();

            }
            break;
        }
    }

    private void upDateCountView() {
        count--;
        countView.setText(String.valueOf(count));

        if (timeLeftAnimationStarted) {
            if (count > START_ANIMATION_COUNT) {
                timeLeftanimatorSet.removeAllListeners();
                timeLeftanimatorSet.end();
                timeLeftanimatorSet.cancel();
                timeLeftAnimationStarted = false;
            }
        } else {
            if (count <= START_ANIMATION_COUNT) {
                timeLeftanimatorSet.addListener(animatorListenerAdapter);
                timeLeftanimatorSet.start();
                timeLeftAnimationStarted = true;
            }
        }


    }
}
