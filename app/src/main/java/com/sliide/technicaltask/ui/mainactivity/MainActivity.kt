package com.sliide.technicaltask.ui.mainactivity

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.sliide.technicaltask.R
import com.sliide.technicaltask.data.model.User
import com.sliide.technicaltask.data.repos.AppRepository
import com.sliide.technicaltask.databinding.ActivityMainBinding
import com.sliide.technicaltask.databinding.AddUserBinding
import com.sliide.technicaltask.ui.mainactivity.adaptor.RecyclerViewAdapter
import com.sliide.technicaltask.utils.Resource
import com.sliide.technicaltask.utils.Utils.validateEmailAddress
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RecyclerViewAdapter.longClickListener{
    private val viewModel: MainViewModel by viewModels()
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var context: Context
    private lateinit var myAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        context = this@MainActivity
        with(mainBinding) {
            floatingButton.setOnClickListener {
                showCustomDialog()
            }
        }
        getUserList()
    }

    private fun setUpRecyclerView(list: List<User>) {
        myAdapter = RecyclerViewAdapter(list as ArrayList, this)
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        with(mainBinding) {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = myAdapter
        }
    }

    private fun getUserList() {
        viewModel.getUserList().observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data!!.let { it1 ->
                        if (it1.size > 0) {
                            setUpRecyclerView(it1.toList())
                        } else {
                            with(mainBinding) {
                                message.setText("${it.message}+/n Empty List!")
                                message.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                            }
                        }
                    }
                    mainBinding.progressBar.visibility = View.GONE
                }
                Resource.Status.ERROR -> {
                    with(mainBinding) {
                        message.setText(it.message)
                        message.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                    }
                }
                Resource.Status.LOADING -> {
                    with(mainBinding) {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun addUser(name: String, email: String, gender: String, status: String) {
        viewModel.addUser(name, email, gender, status).observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    it.data?.let { it1 ->
                        val user = User(it1.id, it1.name, it1.email, it1.gender, it1.status)
                        myAdapter.addUser(user)
                        Toast.makeText(context, getString(R.string.item_added), Toast.LENGTH_SHORT)
                            .show()
                    }
                    mainBinding.progressBar.visibility = View.GONE
                }
                Resource.Status.ERROR -> {
                    with(mainBinding) {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }
                Resource.Status.LOADING -> {
                    with(mainBinding) {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun deleteUser(data: User, position: Int) {
        viewModel.deleteUser(data.id.toString()).observe(this, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    mainBinding.progressBar.visibility = View.GONE
                    myAdapter.removeUser(position)
                    Toast.makeText(context, getString(R.string.item_deleted), Toast.LENGTH_SHORT)
                        .show()
                }
                Resource.Status.ERROR -> {
                    with(mainBinding) {
                        Toast.makeText(context, "${it.message}", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }
                Resource.Status.LOADING -> {
                    with(mainBinding) {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onLongClick(data: User, position: Int) {
        AlertDialog.Builder(context)
            .setTitle(R.string.delete_user)
            .setMessage(R.string.delete_user_message).setPositiveButton(R.string.yes,
                DialogInterface.OnClickListener { dialog, which ->
                    deleteUser(data, position)
                }).setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showCustomDialog() {
        val dialog = Dialog(context, R.style.Theme_Dialog)
        val binding = AddUserBinding.inflate(LayoutInflater.from(context), null, false)!!
        dialog.setContentView(binding.getRoot())
        dialog.setTitle(R.string.add_user)
        dialog.setCancelable(true)
        with(binding) {
            addUser.setOnClickListener(View.OnClickListener {
                if (validateEmailAddress(email.text.toString()) && !TextUtils.isEmpty(
                        name.text.toString()
                    )
                ) {
                    addUser(
                        name.text.toString(),
                        email.text.toString(),
                        gender.selectedItem.toString(),
                        status.selectedItem.toString()
                    )
                    dialog.dismiss()
                } else {
                    if (TextUtils.isEmpty(name.text.toString()))
                        Toast.makeText(
                            context,
                            getString(R.string.empty_name),
                            Toast.LENGTH_SHORT
                        ).show()
                    else if (validateEmailAddress(email.text.toString()))
                        Toast.makeText(
                            context,
                            getString(R.string.correct_email),
                            Toast.LENGTH_SHORT
                        ).show()
                }
            })

            cancel.setOnClickListener(View.OnClickListener {
                dialog.dismiss()
            })
        }
        dialog.show()
    }

}