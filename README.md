#Inject使用手册

首先，在根build.gradle中添加

allprojects {

    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}

然后在app的build.gradle下添加

dependencies {

   	 implementation fileTree(include: ['*.jar'], dir: 'libs')
   	 implementation 'com.github.hrj981671454:Inject:1.0.0'
}

在Activity中的onCreate方法 ViewInjectUtils.inject(this);

@ContentView(R.layout.activity_main) 设置布局

findViewById @ViewInject(R.id.tvText) 设置布局中组件的成员变量

@OnClick({R.id.tvText}) 设置点击事件

@OnLongClick(R.id.tvText) 设置长按事件

