package ru.gimaz.library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.gimaz.library.components.addauthor.AddAuthor
import ru.gimaz.library.components.addauthor.AddAuthorViewModel
import ru.gimaz.library.components.addbook.AddBook
import ru.gimaz.library.components.addbook.AddBookViewModel
import ru.gimaz.library.components.addpublisher.AddPublisher
import ru.gimaz.library.components.addpublisher.AddPublisherViewModel
import ru.gimaz.library.components.author.Author
import ru.gimaz.library.components.author.AuthorViewModel
import ru.gimaz.library.components.authors.Authors
import ru.gimaz.library.components.authors.AuthorsViewModel
import ru.gimaz.library.components.book.Book
import ru.gimaz.library.components.book.BookViewModel
import ru.gimaz.library.components.books.Books
import ru.gimaz.library.components.books.BooksViewModel
import ru.gimaz.library.components.loading.AppLoading
import ru.gimaz.library.components.loading.AppLoadingViewModel
import ru.gimaz.library.components.login.Login
import ru.gimaz.library.components.login.LoginViewModel
import ru.gimaz.library.components.profile.Profile
import ru.gimaz.library.components.profile.ProfileViewModel
import ru.gimaz.library.components.publisher.Publisher
import ru.gimaz.library.components.publisher.PublisherViewModel
import ru.gimaz.library.components.publishers.Publishers
import ru.gimaz.library.components.publishers.PublishersViewModel
import ru.gimaz.library.components.register.Register
import ru.gimaz.library.components.register.RegisterViewModel
import ru.gimaz.library.db.AppDatabase
import ru.gimaz.library.ui.icons.LibraryIcons
import ru.gimaz.library.ui.icons.libraryicons.Book
import ru.gimaz.library.ui.icons.libraryicons.Publisher
import ru.gimaz.library.ui.icons.libraryicons.Writer
import ru.gimaz.library.ui.theme.LibraryTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val db = remember {
                    AppDatabase.getInstance(context)
                }
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.AppLoading.route,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        composable(Screen.AppLoading.route) {
                            val viewModel = remember {
                                AppLoadingViewModel(navController, db.userDao())
                            }
                            AppLoading(viewModel = viewModel)
                        }
                        composable(Screen.Register.route){
                            val viewModel = remember {
                                RegisterViewModel(navController, db.userDao())
                            }
                            Register(viewModel = viewModel)
                        }
                        composable(Screen.Publishers.route) {
                            val viewModel = remember {
                                PublishersViewModel(navController, db.publisherDao())
                            }
                            Publishers(viewModel = viewModel)
                        }
                        composable(Screen.AddPublisher.route) {
                            val viewModel = remember {
                                AddPublisherViewModel(navController, db.publisherDao())
                            }
                            AddPublisher(viewModel = viewModel)
                        }
                        composable(Screen.Books.route) {
                            val viewModel = remember {
                                BooksViewModel(navController, db.bookDao())
                            }
                            Books(viewModel = viewModel)
                        }
                        composable(Screen.Authors.route) {
                            val viewModel = remember {
                                AuthorsViewModel(navController, db.authorDao())
                            }
                            Authors(viewModel = viewModel)
                        }
                        composable(Screen.AddAuthor.route) {
                            val viewModel = remember {
                                AddAuthorViewModel(navController, db.authorDao())
                            }
                            AddAuthor(viewModel = viewModel)
                        }
                        composable(Screen.AddBook.route) {
                            val viewModel = remember {
                                AddBookViewModel(
                                    navController,
                                    db.bookDao(),
                                    db.authorDao(),
                                    db.publisherDao()
                                )
                            }
                            AddBook(viewModel)
                        }
                        composable(
                            Screen.EditPublisher.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            val publisherId = it.arguments?.getInt("id")
                            val viewModel = remember {
                                AddPublisherViewModel(navController, db.publisherDao(), publisherId)
                            }
                            AddPublisher(viewModel = viewModel)
                        }
                        composable(
                            Screen.Publisher.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            val publisherId = it.arguments?.getInt("id")
                            val viewModel = remember {
                                PublisherViewModel(
                                    navController,
                                    publisherId ?: -1,
                                    db.publisherDao(),
                                    db.bookDao()
                                )
                            }
                            Publisher(viewModel = viewModel)
                        }
                        composable(
                            Screen.EditBook.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            val bookId = it.arguments?.getInt("id")
                            val viewModel = remember {
                                AddBookViewModel(
                                    navController,
                                    db.bookDao(),
                                    db.authorDao(),
                                    db.publisherDao(),
                                    bookId
                                )
                            }
                            AddBook(viewModel)
                        }
                        composable(
                            Screen.Book.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            val bookId = it.arguments?.getInt("id")
                            val viewModel = remember {
                                BookViewModel(
                                    navController,
                                    bookId ?: -1,
                                    db.bookDao(),
                                    db.authorDao(),
                                    db.publisherDao()
                                )
                            }
                            Book(viewModel)
                        }
                        composable(
                            Screen.EditAuthor.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            val authorId = it.arguments?.getInt("id")
                            val viewModel = remember {
                                AddAuthorViewModel(navController, db.authorDao(), authorId)
                            }
                            AddAuthor(viewModel = viewModel)
                        }
                        composable(Screen.Author.route,
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) {
                            val authorId = it.arguments?.getInt("id")
                            val viewModel = remember {
                                AuthorViewModel(
                                    navController,
                                    authorId ?: -1,
                                    db.authorDao(),
                                    db.bookDao()
                                )
                            }
                            Author(viewModel = viewModel)
                        }
                        composable(
                            Screen.Login.route
                        ) {
                            val viewModel = remember {
                                LoginViewModel(navController, db.userDao())
                            }
                            Login(viewModel = viewModel)
                        }
                        composable(
                            Screen.Profile.route
                        ){
                            val viewModel = remember {
                                ProfileViewModel(navController)
                            }
                            Profile(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomBarItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null, modifier = Modifier.size(30.dp)) },
                label = { Text(stringResource(item.resourceId),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.basicMarquee()
                    ) },
                selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true,
                onClick = {
                    navController.navigate(item.screen.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String, val routeForFormat: String? = null) {
    data object AddBook : Screen("addBook")
    data object AddAuthor : Screen("addAuthor")
    data object AddPublisher : Screen("addPublisher")
    data object EditPublisher : Screen("editPublisher/{id}", "editPublisher/%s")
    data object EditBook : Screen("editBook/{id}", "editBook/%s")
    data object EditAuthor : Screen("editAuthor/{id}", "editAuthor/%s")
    data object Books : Screen("books")
    data object Authors : Screen("authors")
    data object Publishers : Screen("publishers")
    data object Publisher : Screen("publisher/{id}", "publisher/%s")
    data object Book : Screen("book/{id}", "book/%s")
    data object Author : Screen("author/{id}", "author/%s")
    data object Login: Screen(route = "login")
    data object Register: Screen(route = "register")
    data object AppLoading: Screen(route = "appLoading")

    data object Profile: Screen(route = "profile")
}

sealed class BottomBatItem(
    val screen: Screen,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    data object Books : BottomBatItem(Screen.Books, R.string.books_screen, LibraryIcons.Book)
    data object Authors :
        BottomBatItem(Screen.Authors, R.string.authors_screen, LibraryIcons.Writer)

    data object Publishers :
        BottomBatItem(Screen.Publishers, R.string.publishers_screen, LibraryIcons.Publisher)

    data object Profile: BottomBatItem(Screen.Profile,R.string.profile_screen, Icons.Default.AccountBox )
}

val bottomBarItems = listOf(
    BottomBatItem.Books,
    BottomBatItem.Authors,
    BottomBatItem.Publishers,
    BottomBatItem.Profile
)
