package com.example.nav_components_2_tabs_exercise.screens.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.nav_components_2_tabs_exercise.R
import com.example.nav_components_2_tabs_exercise.Repositories
import com.example.nav_components_2_tabs_exercise.databinding.ActivityMainBinding
import com.example.nav_components_2_tabs_exercise.screens.main.tabs.TabsFragment
import com.example.nav_components_2_tabs_exercise.utils.viewModelCreator
import java.util.regex.Pattern

/**
 * Container for all screens in the app.
 */

class MainActivity : AppCompatActivity() {

    //view-model is used observing username to be displayed in the toolbar
    private val viewModel by viewModelCreator { MainActivityViewModel(Repositories.accountRepository)}

    // nav controller of the current screen
    private var navController: NavController? = null

    private val topLevelDestinations = setOf(getTableDestination(), getSignInDestination())

    //fragment listener is sued for tracking current nav controller
    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks(){
        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            if(f is TabsFragment || f is NavHostFragment) return
            onNavControllerActivated(f.findNavController())
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).also {setContentView(it.root)}
        setSupportActionBar(binding.toolbar)

        //preparing root nav controller
        val navController = getRootNavController()
        prepareRootNavController(isSignedIn(), navController)
        onNavControllerActivated(navController)

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, true)

        // updating username in the toolbar

        viewModel.username.observe(this){
            binding.usernameTextView.text = it
        }
    }


    override fun onDestroy() {
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
        navController = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if(isStartedDestination(navController?.currentDestination)){
            super.getOnBackPressedDispatcher().onBackPressed()
        }else{
            navController?.popBackStack()
        }
    }


    override fun onSupportNavigateUp(): Boolean = (navController?.navigateUp() ?: false || super.onSupportNavigateUp())

    private fun prepareRootNavController(isSignedIn: Boolean, navController: NavController){
        val graph = navController.navInflater.inflate(getMainNavigationGraphId())
        graph.setStartDestination(
            if(isSignedIn){
                getTableDestination()
            }else{
                getSignInDestination()
            }

        )
        navController.graph = graph
    }


    private fun onNavControllerActivated(navController: NavController){
        if(this.navController == navController) return
        this.navController?.removeOnDestinationChangedListener(destinationListener())
        navController.addOnDestinationChangedListener(destinationListener())
        this.navController = navController
    }

    private fun getRootNavController(): NavController{
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        return navHost.navController
    }

    private fun destinationListener() = NavController.OnDestinationChangedListener{_, destination, arguments->
        supportActionBar?.title = prepareTitle(destination.label, arguments)
        supportActionBar?.setDisplayHomeAsUpEnabled(!isStartedDestination(destination))

    }



    private fun isStartedDestination(destination: NavDestination?): Boolean{
        if (destination == null) return false
        val graph = destination.parent ?: return false
        val startDestinations = topLevelDestinations + graph.startDestinationId
        return startDestinations.contains(destination.id)
    }


    private fun prepareTitle(label: CharSequence?, argumets: Bundle?): String{

        //code for this method has been copied from Google sources :)

        if(label == null) return ""
        val title = StringBuffer()
        val fillInPattern = Pattern.compile("\\{(.+?)\\}")
        val matcher = fillInPattern.matcher(label)
        while (matcher.find()){
            val argName = matcher.group(1)
            if(argumets != null && argumets.containsKey(argName)){
                matcher.appendReplacement(title,"")
                title.append(argumets[argName].toString())
            }else{
                throw IllegalStateException(
                    "Could not find $argName in $argumets to fill label $label"
                )
            }
        }
        matcher.appendTail(title)
        return title.toString()
    }

    private fun isSignedIn(): Boolean{
        val bundle = intent.extras ?: throw IllegalStateException("No required arguments")
        val args = MainActivityArgs.fromBundle(bundle)
        return args.isSignedIn
    }

    private fun getMainNavigationGraphId(): Int = R.id.main_graph
    private fun getTableDestination(): Int  = R.id.tabsFragment
    private fun getSignInDestination(): Int = R.id.signInButton
}