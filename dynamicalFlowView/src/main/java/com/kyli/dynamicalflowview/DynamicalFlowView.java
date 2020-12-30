package com.kyli.dynamicalflowview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.IntDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class DynamicalFlowView extends ViewGroup implements View.OnClickListener {
    public  interface  onSelectLisener{
        void  onSelected(int index);

    }
    public static final int MULTIPLE = 0;
    public static final int SINGLE = 1;

    @IntDef({SINGLE, MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    public @interface CheckedMode {

    }

    private int currentChooiceMode = MULTIPLE;
    private final String exceptionInfo =
            "****计****\n" +
                    "***算***\n" +
                    "**错**\n" +
                    "*误*\n" +
                    "*";
    private Context context;
    private List<String> labelData;

    private onSelectLisener    onSelectLisener;

    private enum State {
        FIXED_SIZE,
        AUTO_SIZE
    }

    private State state = State.AUTO_SIZE;

    private Drawable itemBackGroud = null;
    private Drawable itemSelectedBackGroud = null;
    private int itemTextColor = Color.RED;
    private int itemTextSelectedColor = Color.YELLOW;

    private int spaceForWidth = 0;
    private int spaceForHeight = 0;
    private int textSize = 14;
    private int textPaddingLeft = 0;
    private int textPaddingRight = 0;
    private int textPaddingTop = 0;
    private int textPaddingBottom = 0;

    private int textMarginLeft = 0;
    private int textMarginRight = 0;
    private int textMarginTop = 0;
    private int textMarginBottom = 0;
    private FixedParams fixedParams;

    private List<Integer> choosedViewIndex = new ArrayList<>();


    /*固定模式下生效的参数*/
    private static class FixedParams {
        int labelHeight = 0;
        int labelWidht = 0;
    }


    public DynamicalFlowView(Context context) {
        this(context, null);


    }

    public DynamicalFlowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicalFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.dynamicalFlowView, 0, 0);
        try {


            int stateValue = typedArray.getInteger(R.styleable.dynamicalFlowView_labelState, 1);
            state = State.values()[stateValue];
            if (state == State.FIXED_SIZE) {
                fixedParams = new FixedParams();
                fixedParams.labelHeight = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelHeight, 0);
                fixedParams.labelWidht = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelWidth, 0);
            }
            spaceForWidth = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelWidthSpace, 0);
            spaceForHeight = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelHeightSpace, 0);
            itemBackGroud = typedArray.getDrawable(R.styleable.dynamicalFlowView_labelBackGroud);
            itemSelectedBackGroud = typedArray.getDrawable(R.styleable.dynamicalFlowView_labelSelectedBackGroud);
            itemTextSelectedColor = typedArray.getColor(R.styleable.dynamicalFlowView_labelSelectedTextColor, Color.YELLOW);
            itemTextColor = typedArray.getColor(R.styleable.dynamicalFlowView_labelTextColor, Color.RED);
            spaceForWidth = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelWidthSpace, 0);
            spaceForHeight = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelHeightSpace, 0);
            textSize = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextSize, 14);
            textPaddingLeft = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextPaddingLeft, 0);
            textPaddingRight = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextPaddingRight, 0);
            textPaddingTop = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextPaddingTop, 0);
            textPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextPaddingBottom, 0);
            textMarginLeft = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextMarginLeft, 0);
            textMarginRight = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextMarginRight, 0);
            textMarginTop = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextMarginTop, 0);
            textMarginBottom = typedArray.getDimensionPixelSize(R.styleable.dynamicalFlowView_labelTextMarginBottom, 0);

        } finally {
            typedArray.recycle();
        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widhtSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int lineWidht = 0;/*单行宽*/
        int lineHeight = 0;/*单行高*/
        int freeWidhtSpace = getWidthFreeSpace(widhtSize);
        int maxWidht = 0;/*单行最大宽度*/
        int maxHeight = 0;/*单行最大高度*/


        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            /*测量ziview*/
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();//指和父类的关联
            int childWidht = child.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            int childHeight = child.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;

            if (childWidht + lineWidht > freeWidhtSpace) {
                //上一行减去尾部的space
                lineWidht -= spaceForWidth;
                maxWidht = Math.max(lineWidht, maxWidht);
                lineWidht = 0;
                lineHeight = 0;
                maxHeight += spaceForHeight;
            }
            if (childHeight > lineHeight) {
                maxHeight -= lineHeight;//换行后肯定是0  肯定会执行一次
                maxHeight += childHeight;
                lineHeight = childHeight;
            }
            lineWidht += childWidht;//增加水平间距

            if (i + 1 < getChildCount()) {
                lineWidht += spaceForWidth;
            } else {
                maxWidht = Math.max(lineWidht, maxWidht);
            }


        }
        setMeasuredDimension(widthMode == MeasureSpec.EXACTLY ? widhtSize : maxWidht + getPaddingLeft() + getPaddingRight(),
                heightMode == MeasureSpec.EXACTLY ? heightSize : maxHeight + getPaddingTop() + getPaddingBottom()
        );
    }

    private int getWidthFreeSpace(int maxWidhtSize) {
        return maxWidhtSize - getPaddingLeft() - getPaddingRight();
    }

    private int getHeightFreeSpace(int maxHeightSize) {
        return maxHeightSize - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public LayoutParams generateLayoutParams(LayoutParams attrs) {
        return new MarginLayoutParams(attrs);
    }

    /**/

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int startLeftPoint = getPaddingLeft();/*起点*/
        int startTopPoint = getPaddingTop();
        /*摆放位置*/
        int lineWidth = 0;
        int lineHeight = 0;
        int vl = startLeftPoint;
        int vt = startTopPoint;

        int vr = 0;
        int vb = 0;


        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) continue;


            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            /*每个控件所占的宽*/
            int childWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            /*每个控件死活站的高*/
            int childHeight = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;

            if (childWidth + lineWidth > getWidth() - getPaddingLeft() - getPaddingRight()) {

                //换行  重置起点
                vl = startLeftPoint;
                /*加上上一行最大高度*/
                vt += (lineHeight + spaceForHeight);
                lineWidth = 0;//重置我们的线宽
                lineHeight = childHeight;
            }


            vl += layoutParams.leftMargin;
            vr = vl + child.getMeasuredWidth();
            vb = vt + child.getMeasuredHeight();
            lineWidth += childWidth;
            lineWidth += spaceForWidth;
            lineHeight = Math.max(childHeight, lineHeight);
            /*控件的位置*/
            child.layout(vl, vt, vr, vb);
            vl = lineWidth;

        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    /*设置参数的时候*/

    public void setLabelData(List<String> labelData) {
        this.labelData = labelData;
        removeAllViews();
        if (labelData == null || labelData.size() == 0) return;
        for (int i = 0; i < labelData.size(); i++) {
            TextView textView = new TextView(context);
            textView.setText(labelData.get(i));
            textView.getPaint().setTextSize(textSize);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (state == State.FIXED_SIZE) {
                layoutParams = new LinearLayout.LayoutParams(fixedParams.labelWidht, fixedParams.labelHeight);
            }
            layoutParams.setMargins(textMarginLeft, textMarginTop, textMarginRight, textMarginBottom);
            textView.setLayoutParams(layoutParams);
            textView.setOnClickListener(this);
            textView.setTag(i);
            textView.setGravity(Gravity.CENTER);
            textView.setBackground(itemBackGroud);

            textView.setTextColor(itemTextColor);
            textView.setPadding(textPaddingLeft, textPaddingTop, textPaddingRight, textPaddingBottom);
            addView(textView);
        }


    }

    public void setOnSelectLisener(DynamicalFlowView.onSelectLisener onSelectLisener) {
        this.onSelectLisener = onSelectLisener;
    }

    @Override
    public void onClick(View v) {
        int tag = (int) v.getTag();//绑定序号
        TextView t = (TextView) v;
        if (choosedViewIndex.contains(tag)) {
            noChoiceView(t);
            /*必须是用object  否则 会当下表移除*/
            choosedViewIndex.remove((Integer) tag);
        } else {
            choiceView(t);
            onCallSelectedLisener(tag);
            choosedViewIndex.add(tag);
        }
        if (currentChooiceMode == SINGLE) {
            //单选的话  我们处理掉倒数第2个
            int i = choosedViewIndex.indexOf(tag);
            if (i > 0) {
                Integer integer = choosedViewIndex.get(i - 1);
                TextView cancleView = (TextView) getChildAt(integer);
                noChoiceView(cancleView);
                choosedViewIndex.remove(integer);/*对象移除*/
            }
        }

    }
    private  void  onCallSelectedLisener(int  index){
        if(onSelectLisener !=null){
            onSelectLisener.onSelected(index);
        }
    }
    private void choiceView(TextView textView) {
        textView.setTextColor(itemTextSelectedColor);
        textView.setBackground(itemSelectedBackGroud);
    }

    private void noChoiceView(TextView textView) {
        textView.setTextColor(itemTextColor);
        textView.setBackground(itemBackGroud);
    }

    @Override
    public void addView(View child) {
        super.addView(child);

    }


    /*切换到单选的时候回保留一个选择的*/
    public void setChooiceMode(@CheckedMode int mode) {
        if (this.currentChooiceMode != mode) {
            if (mode == SINGLE) {

                for (int i = choosedViewIndex.size() - 2; i >= 0; i--) {
                        TextView  cancleView= (TextView) getChildAt(i);
                        noChoiceView(cancleView);
                        choosedViewIndex.remove(i);

                }
            }
            this.currentChooiceMode = mode;
        }
    }
    /*你可以获取到当前选中的那些下表*/
    public  List<Integer> getSelectedIndex(){
        return  choosedViewIndex;
    }
}
