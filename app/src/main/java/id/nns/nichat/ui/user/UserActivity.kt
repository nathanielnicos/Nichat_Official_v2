package id.nns.nichat.ui.user

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import id.nns.nichat.R
import id.nns.nichat.databinding.ActivityUserBinding
import id.nns.nichat.domain.model.User
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.ui.chat.ChatActivity
import id.nns.nichat.utils.OnSomethingClickListener
import id.nns.nichat.viewmodel.ViewModelFactory

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var adapter: UserAdapter
    private lateinit var userPreference: UserPreference
    private lateinit var userViewModel: UserViewModel

    private var users = ArrayList<User>()
    private var clickedUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]
        userPreference = UserPreference(this)

        setAdapter()
        observeValue()
        onClickUser(userPreference.getUser().uid.toString())
    }

    private fun setAdapter() {
        adapter = UserAdapter()
        binding.rvUsers.adapter = adapter
        binding.rvUsers.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun onClickUser(myId: String) {
        adapter.setOnUserClickListener(object: OnSomethingClickListener {
            override fun onSomethingClick(view: View, position: Int) {
                clickedUser = adapter.users[position]
                binding.pbUser.visibility = View.VISIBLE
                userViewModel.checkExistingChannel(myId, adapter.users[position].uid)
            }
        })
    }

    private fun observeValue() {
        userViewModel.failureMessage.observe(this) { msg ->
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        userViewModel.isExist.observe(this) { isExist ->
            if (isExist) {
                Intent(this@UserActivity, ChatActivity::class.java).apply {
                    putExtra(ChatActivity.KEY_USER, clickedUser)
                    startActivity(this)
                }
                finish()
            } else {
                Toast.makeText(
                    this@UserActivity,
                    "Please wait ...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        userViewModel.allUsers.observe(this) { arrayList ->
            arrayList.forEach { user ->
                users.add(user)
            }

            adapter.users = arrayList

            if (arrayList.isNotEmpty()) {
                binding.animationEmpty.visibility = View.GONE
            }
        }

        userViewModel.query.observe(this) { text ->
            val searchedUsers = ArrayList<User>()

            users.forEach { user ->
                if (user.name.lowercase().contains(text.lowercase(), true)) {
                    searchedUsers.add(user)
                }
            }

            adapter.users = searchedUsers
            binding.animationEmpty.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search_item)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                userViewModel.showSearchedUsers(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

}
