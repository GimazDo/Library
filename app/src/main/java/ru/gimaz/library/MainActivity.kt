package ru.gimaz.library

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
import ru.gimaz.library.components.publisher.Publisher
import ru.gimaz.library.components.publisher.PublisherViewModel
import ru.gimaz.library.components.publishers.Publishers
import ru.gimaz.library.components.publishers.PublishersViewModel
import ru.gimaz.library.db.AppDatabase
import ru.gimaz.library.ui.theme.LibraryTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LibraryTheme {
                val navController = rememberNavController()
                val db = remember {
                    AppDatabase.getInstance(this)
                }
                Surface {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Authors.route,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        composable(Screen.AddBook.route) {
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

                    }
                }
            }
        }
    }
}


@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        bottomBarItems.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(item.resourceId)) },
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
}

sealed class BottomBatItem(
    val screen: Screen,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    data object Books : BottomBatItem(Screen.Books, R.string.books_screen, Icons.Filled.Search)
    data object Authors :
        BottomBatItem(Screen.Authors, R.string.authors_screen, Icons.Filled.Search)

    data object Publishers :
        BottomBatItem(Screen.Publishers, R.string.publishers_screen, Icons.Filled.Search)
}

val bottomBarItems = listOf(
    BottomBatItem.Books,
    BottomBatItem.Authors,
    BottomBatItem.Publishers
)
