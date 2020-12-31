# KDynamicalFlowView

##    流式布局  
####   gradle依赖方式
             ** 全局gralde文件下 添加**
                                
                allprojects {
                    repositories {
                        google()
                
                        jcenter()
                     **   maven { url 'https://jitpack.io' }**
                    }
                
                }
             **implementation 'com.github.Kasarulor:KDynamicalFlowView:1.0.6' **
             特别说明基于1.0.6  之前的版本都会出现  resolve fail      
             这里也特别记录下这个坑  因为我再上传github的时候忽略掉了 gradle.properties   因此导致编译包的时候出现了 没找到这个文件 
####   支持单选和多选
        #####特别说明下  单选和多选模式切换的时候  不会清空所有的已选择状态!  但是多选切换单选会保留最后一条  你可以选择调用 方法getSelectedIndex() **
#####  exe:
        
         <com.kyli.dynamicalflowview.DynamicalFlowView
                android:id="@+id/label"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
####  java:
           DynamicalFlowView  dyview= findViewById(R.id.label);
            dyview.setLabelData(new  arraylist<String>()); 
#####  属性明细
              <com.kyli.dynamicalflowview.DynamicalFlowView
                    android:id="@+id/label"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="10dp"
                    app:labelTextColor="@color/black3"
                    app:labelTextSize="12sp"         //label字体大小
                    app:labelSelectedBackGroud="@color/themeColor"//背景颜色
                    app:labelSelectedTextColor="@android:color/white"//选中时的颜色
                    app:labelHeightSpace="10dp"//label之间的上下间距(margin会导致最上边一行和最下边一行出现间距 而且和中间不一样  所以不喜欢把    space是对于水平方向只是增加label之间的距离  不会增减两边的)
                    app:labelBackGroud="@color/E6"//label背景色选中状态
                    app:labelWidthSpace="10dp"//水平控件间间距
                    app:labelTextPaddingLeft="5dp"//文本内间距
                    app:labelTextPaddingRight="5dp"
                    app:labelTextPaddingTop="2dp"
                    app:labelTextPaddingBottom="2dp"
                    //整体控件间距
                    android:background="@android:color/white"  />
                    
                    
  #####    支持两种模式
       <attr format="enum" name="labelState">
              <enum name="FIXED_SIZE" value="0"/>
              <enum name="AUTO_SIZE" value="1"/>
  
          </attr>
          **  fixed-size   必须提供  labelHeight  labelWidth  这样能保证每个label的大小一样   **
          **  auto-size   会自动根据文本内容+文本内间距测量大小**
  