package com.example.rma_spirala

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.SystemClock
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasChildCount
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//ALL PASS - CHECKED ned 28.04.
@RunWith(AndroidJUnit4::class)
class Spirala2TestoviMoji {
    @get:Rule
    var activityRule: ActivityScenarioRule<NovaBiljkaActivity> =
        ActivityScenarioRule(NovaBiljkaActivity::class.java)
    private var decorView: View? = null

    @Before
    fun setUp() {
        activityRule.scenario.onActivity({ activity ->
            decorView = activity.getWindow().getDecorView()
        })
    }

    fun setBitmap(bitmap: Bitmap): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set bitmap on ImageView"
            }

            override fun getConstraints(): org.hamcrest.Matcher<View>? {
                return isAssignableFrom(ImageView::class.java)
            }

            override fun perform(uiController: UiController, view: android.view.View) {
                (view as ImageView).setImageBitmap(bitmap)
            }
        }
    }

    @Test //PASS
    fun validacijaEditTekstova() {
        //provjera [2,20] ranga

        onView(withId(R.id.nazivET)).perform(typeText("Neki naziv biljke koji je previse dug"))
        onView(withId(R.id.porodicaET)).perform(typeText("k"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("Neki tekst za medicinsko upozorenje"))

        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo())
        //ukloni tastaturu koja smeta
        onView(withId(android.R.id.content)).perform(ViewActions.pressBack())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())

        //uporedi da li error
        onView(withId(R.id.nazivET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error.toString()
                assertEquals("Dužina teksta mora biti između 2 i 20!", error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }
        onView(withId(R.id.porodicaET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error.toString()
                assertEquals("Dužina teksta mora biti između 2 i 20!", error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }
        onView(withId(R.id.medicinskoUpozorenjeET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error.toString()
                assertEquals("Dužina teksta mora biti između 2 i 20!", error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }

    }

    @Test //PASS
    fun testCameraIntent() {

        val dummyImageUri = Uri.parse("content://dummy/image")

        //dummy for testiranje
        val dummyBitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        // Mock camera intent
        Intents.init()
        Intents.intending(IntentMatchers.hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent().apply {
                putExtra(MediaStore.EXTRA_OUTPUT, dummyImageUri)
            })
        )

        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo())
        onView(withId(R.id.slikaIV)).perform(setBitmap(dummyBitmap))
        onView(withId(R.id.slikaIV)).check(matches(isDisplayed()))

        Intents.release()
    }

    @Test //PASS
    fun validacijaEditTekstoviOkListeNe() {
        //ET pravilno uneseno
        onView(withId(R.id.nazivET)).perform(typeText("Neki"))
        onView(withId(R.id.porodicaET)).perform(typeText("porodica"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("medicinsko"))

        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo())
        //ukloni tastaturu koja smeta
        onView(withId(android.R.id.content)).perform(ViewActions.pressBack())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())

        //uporedi da li error
        onView(withId(R.id.nazivET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error
                assertNull(error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }
        onView(withId(R.id.porodicaET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error
                assertNull(error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }
        onView(withId(R.id.medicinskoUpozorenjeET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error
                assertNull(error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }

        // Check if ListViews are unchecked
        onView(withId(R.id.profilOkusaLV)).check { view, _ ->
            // Check if ListView is unchecked
            if (view is ListView) {
                assertEquals(ListView.INVALID_POSITION, view.checkedItemPosition)
            } else {
                throw IllegalStateException("View is not an instance of ListView")
            }
        }
        // Check if multiple choice ListViews have no items checked
        val listViews = arrayOf(R.id.medicinskaKoristLV, R.id.klimatskiTipLV, R.id.zemljisniTipLV)

        listViews.forEach { listViewId ->
            onView(withId(listViewId)).check { view, _ ->
                // Check if ListView is multiple choice
                if (view is ListView && view.choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                    val checkedItemCount = view.checkedItemCount
                    assertEquals(0, checkedItemCount)
                } else {
                    throw IllegalStateException("View is not an instance of ListView or is not in multiple choice mode")
                }
            }
        }

        onView(withText("Lista jela je prazna!"))
            .inRoot(ToastMatcher().apply {
                (matches(isDisplayed()))
            })
        SystemClock.sleep(2000)
        onView(withText("Nije selektovana niti jedna medicinska korist!"))
            .inRoot(ToastMatcher().apply {
                (matches(isDisplayed()))
            })
        SystemClock.sleep(2000)
        onView(withText("Nije selektovan niti jedan klimatski tip!"))
            .inRoot(ToastMatcher().apply {
                (matches(isDisplayed()))
            })
        SystemClock.sleep(2000)
        onView(withText("Nije selektovan niti jedan zemljišni tip!"))
            .inRoot(ToastMatcher().apply {
                (matches(isDisplayed()))
            })

    }

    @Test //PASS
    fun dodavanjeIstogJela() {
        //provjera za dodavanje istog jela
        //provjera za jelo 2,20 opseg

        onView(withId(R.id.jeloET)).perform(typeText("Vocna salata"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(withId(R.id.jeloET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error
                assertNull(error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }

        onView(withId(R.id.jeloET)).perform(typeText("Vocna salata"))
        onView(withId(R.id.dodajJeloBtn)).perform(click())

        onView(withId(R.id.jeloET))
            .check(matches(ViewMatchers.hasErrorText("To jelo se već nalazi u listi jela!")))

        onView(withId(R.id.uslikajBiljkuBtn)).perform(scrollTo())
        //provjera je li jelo u LV jela -- to je ujedno i jedno jedino jelo
        onView(withId(R.id.jelaLV))
            .check(matches(hasDescendant(withText("Vocna salata"))))
            .check(matches(isDisplayed()))
            .check(matches(hasChildCount(1)))
    }

    @Test
    fun validacijaListViews() {

        val medicinskiLV = withId(R.id.medicinskaKoristLV)
        val positionToCheck = 2

        // simulacija klika na poziciju
        onData(anything())
            .inAdapterView(medicinskiLV)
            .atPosition(positionToCheck)
            .perform(ViewActions.click())

        onData(anything())
            .inAdapterView(medicinskiLV)
            .atPosition(positionToCheck)
            .check(matches(isChecked()))

        val listViews = arrayOf(R.id.klimatskiTipLV, R.id.zemljisniTipLV)

        listViews.forEach { listViewId ->
            onView(withId(listViewId)).check { view, _ ->
                // Check if ListView is multiple choice
                if (view is ListView && view.choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                    val checkedItemCount = view.checkedItemCount
                    assertEquals(0, checkedItemCount)
                } else {
                    throw IllegalStateException("View is not an instance of ListView or is not in multiple choice mode")
                }
            }
        }

        // Check if there are any toast messages
        onView(withText("Nije selektovana niti jedna medicinska korist!"))
            .inRoot(ToastMatcher().apply {
                (matches(not((isDisplayed()))))
            })
        onView(withText("Nije selektovan niti jedan klimatski tip!"))
            .inRoot(ToastMatcher().apply {
                (matches(isDisplayed()))
            })
        onView(withText("Nije selektovan niti jedan zemljišni tip!"))
            .inRoot(ToastMatcher().apply {
                (matches(isDisplayed()))
            })
    }

    @Test //pass
    fun provjeraValidacijeMix() {
        //mix provjera nesto ok nesto ne
        //ET pravilno uneseno
        onView(withId(R.id.nazivET)).perform(typeText("Neki"))
        onView(withId(R.id.porodicaET)).perform(typeText("p"))
        onView(withId(R.id.medicinskoUpozorenjeET)).perform(typeText("medicinsko hj jhhj jh ihui i"))

        onView(withId(R.id.dodajBiljkuBtn)).perform(scrollTo())
        //ukloni tastaturu koja smeta
        onView(withId(android.R.id.content)).perform(ViewActions.pressBack())
        onView(withId(R.id.dodajBiljkuBtn)).perform(click())

        //uporedi da li error
        onView(withId(R.id.nazivET)).check { view, _ ->
            // Check if setError is called
            if (view is EditText) {
                val error = view.error
                assertNull(error)
            } else {
                throw IllegalStateException("View is not an instance of EditText")
            }
        }
        onView(withId(R.id.porodicaET))
            .check(matches(ViewMatchers.hasErrorText("Dužina teksta mora biti između 2 i 20!")))

        onView(withId(R.id.medicinskoUpozorenjeET))
            .check(matches(ViewMatchers.hasErrorText("Dužina teksta mora biti između 2 i 20!")))

        val listViews = arrayOf(R.id.medicinskaKoristLV, R.id.klimatskiTipLV)

        listViews.forEach { listViewId ->
            onView(withId(listViewId)).check { view, _ ->
                // Check if ListView is multiple choice
                if (view is ListView && view.choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                    val checkedItemCount = view.checkedItemCount
                    assertEquals(0, checkedItemCount)
                } else {
                    throw IllegalStateException("View is not an instance of ListView or is not in multiple choice mode")
                }
            }
        }

        onView(withText("Nije selektovana niti jedna medicinska korist!"))
            .inRoot(ToastMatcher().apply {
                (matches(not((isDisplayed()))))
            })
        onView(withText("Nije selektovan niti jedan klimatski tip!"))
            .inRoot(ToastMatcher().apply {
                (matches(isDisplayed()))
            })
    }

}