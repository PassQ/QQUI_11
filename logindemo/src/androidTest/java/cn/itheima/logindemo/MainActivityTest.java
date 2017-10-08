package cn.itheima.logindemo;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {

        // 查找id为et_username的编辑框
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_username),
                        isDisplayed()));

        // 模拟人工的输入用户名：admin
        appCompatEditText.perform(replaceText("admin"), closeSoftKeyboard());

        // 查找id为et_password的编辑框
        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_password),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));

        // 模拟人工的输入密码：admin
        appCompatEditText2.perform(replaceText("123"), closeSoftKeyboard());

        // 查找id为btn_login的按钮
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_login), withText("登录"),
                        withParent(allOf(withId(R.id.activity_main),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));

        // 模拟人工的点击登录按钮
        appCompatButton.perform(click());

        // 查找id为tv_result的TextView
        ViewInteraction textView = onView(
                allOf(withId(R.id.tv_result), withText("登录成功"),
                        childAtPosition(
                                allOf(withId(R.id.activity_main),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                0),
                        isDisplayed()));

        // 断言：如果登录成功，TextView显示的内容应该为："登录成功"
        // 如果确实是该字符串，则测试通过，界面会显示测试成功的绿条
        // 否则，则测试不通过，界面会显示测试失败的红条
        textView.check(matches(withText("登录成功")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
