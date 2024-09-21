package com.tdcolvin.contentproviderclientdemo

import android.app.Application
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BirthdaysClientViewModel(app: Application): AndroidViewModel(app) {
    val birthdates: MutableStateFlow<List<UserBirthdate>> = MutableStateFlow(emptyList())
    val error: MutableStateFlow<String?> = MutableStateFlow(null)

    fun loadBirthdates() {
        error.value = null

        viewModelScope.launch(Dispatchers.IO) {
            // Get the Context's ContentResolver, which connects us with the ContentProvider in the
            // demo app
            val contentResolver = getApplication<Application>().contentResolver

            val loadedBirthdates = mutableListOf<UserBirthdate>()

            var cursor: Cursor? = null
            try {
                // Query the other app's database via its exported content provider, asking for all
                // birthdates
                cursor = contentResolver.query(
                    Uri.parse("content://com.tdcolvin.contentproviderdemo/birthdates"),
                    arrayOf("id", "name", "birthdate"), // request these columns in this order
                    null,   //no query - just get everything
                    null
                )

                while (cursor?.moveToNext() == true) {
                    // For each result, parse it and add it to the loadedBirthdates list
                    loadedBirthdates += UserBirthdate(
                        name = cursor.getString(1),
                        birthdate = LocalDate.parse(cursor.getString(2))
                    )
                }
            }
            catch (e: Exception) {
                error.value = "Unable to load data. Please check ContentProviderDemo app is installed"
            }
            finally {
                // Remember to close the cursor when we're finished with it
                cursor?.close()
            }

            // On completion update the UI state
            birthdates.value = loadedBirthdates.toList()
        }
    }

    fun addNewRandomBirthdate() {
        viewModelScope.launch(Dispatchers.IO) {
            // Generate random data to insert
            val name = names.random()
            val birthdate = LocalDate.ofEpochDay((Math.random() * 365 * 50).toLong()).toString()

            // Get the Context's ContentResolver, which connects us with the ContentProvider in the
            // demo app
            val contentResolver = getApplication<Application>().contentResolver

            // Using the ContentResolver, insert the random data
            contentResolver.insert(
                Uri.parse("content://com.tdcolvin.contentproviderdemo/birthdates"),
                ContentValues().apply {
                    put("name", name)
                    put("birthdate", birthdate)
                }
            )
        }.invokeOnCompletion {
            // ...and finish up by refreshing the list.
            loadBirthdates()
        }
    }
}

data class UserBirthdate(
    val name: String,
    val birthdate: LocalDate
)

private val names = listOf("James", "Mary", "Michael", "Patricia", "Robert", "Jennifer", "John", "Linda", "David", "Elizabeth", "William", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas", "Karen", "Christopher", "Sarah", "Charles", "Lisa", "Daniel", "Nancy", "Matthew", "Sandra", "Anthony", "Betty", "Mark", "Ashley", "Donald", "Emily", "Steven", "Kimberly", "Andrew", "Margaret", "Paul", "Donna", "Joshua", "Michelle", "Kenneth", "Carol", "Kevin", "Amanda", "Brian", "Melissa", "Timothy", "Deborah", "Ronald", "Stephanie", "George", "Rebecca", "Jason", "Sharon", "Edward", "Laura", "Jeffrey", "Cynthia", "Ryan", "Dorothy", "Jacob", "Amy", "Nicholas", "Kathleen", "Gary", "Angela", "Eric", "Shirley", "Jonathan", "Emma", "Stephen", "Brenda", "Larry", "Pamela", "Justin", "Nicole", "Scott", "Anna", "Brandon", "Samantha", "Benjamin", "Katherine", "Samuel", "Christine", "Gregory", "Debra", "Alexander", "Rachel", "Patrick", "Carolyn", "Frank", "Janet", "Raymond", "Maria", "Jack", "Olivia", "Dennis", "Heather", "Jerry", "Helen", "Tyler", "Catherine", "Aaron", "Diane", "Jose", "Julie", "Adam", "Victoria", "Nathan", "Joyce", "Henry", "Lauren", "Zachary", "Kelly", "Douglas", "Christina", "Peter", "Ruth", "Kyle", "Joan", "Noah", "Virginia", "Ethan", "Judith", "Jeremy", "Evelyn", "Christian", "Hannah", "Walter", "Andrea", "Keith", "Megan", "Austin", "Cheryl", "Roger", "Jacqueline", "Terry", "Madison", "Sean", "Teresa", "Gerald", "Abigail", "Carl", "Sophia", "Dylan", "Martha", "Harold", "Sara", "Jordan", "Gloria", "Jesse", "Janice", "Bryan", "Kathryn", "Lawrence", "Ann", "Arthur", "Isabella", "Gabriel", "Judy", "Bruce", "Charlotte", "Logan", "Julia", "Billy", "Grace", "Joe", "Amber", "Alan", "Alice", "Juan", "Jean", "Elijah", "Denise", "Willie", "Frances", "Albert", "Danielle", "Wayne", "Marilyn", "Randy", "Natalie", "Mason", "Beverly", "Vincent", "Diana", "Liam", "Brittany", "Roy", "Theresa", "Bobby", "Kayla", "Caleb", "Alexis", "Bradley", "Doris", "Russell", "Lori")
