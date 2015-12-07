package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.dev.DevUtils;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.callbacks.SceneCallbacks;

/**
 * TODO Consume "onBackPressed" in activity. -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 * Implement method on {@link MultiSubSectionScene}. If current {@link SceneImpl} is 0, and back is pressed,
 * allow activity to swap to previous step. Else, let {@link MultiSubSectionScene} go back a {@link SceneImpl}.
 */

public abstract class MultiSubSectionScene<T> extends SceneImpl<T> implements SceneCallbacks
{

    private SceneAnimator animator;

    public MultiSubSectionScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public void onPreInitialized()
    {
        super.onPreInitialized();
        animator = new SceneAnimator(this);
    }

    @Override
    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.fragment_step_multi_scene, parent, true);
    }

    @Override
    public void onSceneCreated(View scene)
    {
        int current = getCurrentPosition();
        showScene(current, false);
    }

    public int getCurrentPosition()
    {
        return getTag() == null ? 0 : (int) getTag();
    }

    public void setCurrentPosition(int pos)
    {
        setTag(pos);
    }

    public abstract int getSceneCount();

    public abstract Scene onCreateScene(LayoutInflater inflater, int position);

    /**
     * This method is responsible for telling any subclasses that this scene was just popped off
     * the view stack and a new one is being added shorty. If you need to get the result and save
     * it, do that now.
     */
    public void onSceneChanged(Scene scene, Scene newScene) { }

    //TODO Queue transition to a handler instead of ignoring the request if already transitioning
    public void showScene(int position, boolean withAnimation)
    {
        // If the count is 0, crash!
        if(getSceneCount() < 1)
        {
            throw new IllegalStateException("Scene count cannot be 0");
        }

        // If the view is animating, ignore that the request was made
        if (animator.isAnimating())
        {
            return;
        }

        int direction = getCurrentPosition() < position ?
                SceneAnimator.SHIFT_LEFT :
                SceneAnimator.SHIFT_RIGHT ;

        LayoutInflater inflater = LayoutInflater.from(getContext());

        Scene oldScene = (Scene) findViewById(R.id.current_child_scene);
        if (oldScene != null) { oldScene.getView().setId(R.id.old_child_scene);}

        Scene newScene = onCreateScene(inflater, position);
        newScene.getView().setId(R.id.current_child_scene);
        newScene.setCallbacks(this);

        if (withAnimation && oldScene != null)
        {
            animator.animate(oldScene, newScene, direction);
        }
        else
        {
            animator.setIsAnimating(false);

            removeAllViews();
            addView(newScene.getView());
        }

        onSceneChanged(oldScene, newScene);

        // Save the current position
        setCurrentPosition(position);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (getChildCount() >= 2) {
            throw new IllegalStateException("Can't add more than 2 views to a " + MultiSubSectionScene.class.getSimpleName());
        }
        super.addView(child, index, params);
    }

    public void loadNextScene()
    {
        int currentScene = getCurrentPosition();
        showScene(currentScene + 1, true);
    }

    public void loadPreviousScene()
    {
        int currentScene = getCurrentPosition();
        showScene(currentScene - 1 , true);
    }

    @Override
    public boolean isBackEventConsumed()
    {
        if (getCurrentPosition() > 0)
        {
            loadPreviousScene();
            return true;
        }

        return super.isBackEventConsumed();
    }

    @Override
    public void onNextPressed(Step step)
    {
        if(getCurrentPosition() < getSceneCount() - 1)
        {
            loadNextScene();
        }
        else
        {
            getCallbacks().onNextPressed(getStep());
        }
    }

    /**
     * Pass the title through to the host (the activity).
     * @param title
     */
    @Override
    public void onStepTitleChanged(String title)
    {
        getCallbacks().onStepTitleChanged(title);
    }

    @Override
    public void onCancelStep()
    {
        getCallbacks().onCancelStep();
    }

    @Override
    public void onStepResultChanged(Step step, StepResult result)
    {
//        TODO Implement
        DevUtils.throwUnsupportedOpException();
    }

    @Override
    public void onSkipStep(Step step)
    {
//        TODO Implement
        DevUtils.throwUnsupportedOpException();
    }

    @Override
    public StepResult getResultStep(String stepId)
    {
//        TODO Implement
        DevUtils.throwUnsupportedOpException();
        return null;
    }

}
