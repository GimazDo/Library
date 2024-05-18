package ru.gimaz.library.components.addbook

import android.content.Context
import android.net.Uri
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.db.Author
import ru.gimaz.library.db.AuthorDao
import ru.gimaz.library.db.Book
import ru.gimaz.library.db.BookDao
import ru.gimaz.library.db.Publisher
import ru.gimaz.library.db.PublisherDao
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState
import ru.gimaz.library.enums.ProcessState
import ru.gimaz.library.util.loadBitmapToFile

class AddBookViewModel(
    private val navController: NavController,
    private val bookDao: BookDao,
    private val authorDao: AuthorDao,
    private val publisherDao: PublisherDao,
    private val bookId: Int? = null
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _author = MutableStateFlow<Author?>(null)
    val author = _author.asStateFlow()

    private val _publisher = MutableStateFlow<Publisher?>(null)
    val publisher = _publisher.asStateFlow()

    private val _publishYear = MutableStateFlow<Int?>(null)
    val publishYear = _publishYear.asStateFlow()

    private val _pages = MutableStateFlow<Int?>(null)
    val pages = _pages.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath = _imagePath.asStateFlow()

    private val _authors = MutableStateFlow(emptyList<Author>())
    val authors = _authors.asStateFlow()
    private val _publishers = MutableStateFlow(emptyList<Publisher>())
    val publishers = _publishers.asStateFlow()


    private val _processState = MutableStateFlow(ProcessState.IDLE)
    val savingState = _processState.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()
    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState = _loadingState.asStateFlow()

    private val _objectLoadingError = MutableStateFlow<LoadingObjectError?>(null)
    val objectLoadingError = _objectLoadingError.asStateFlow()

    private val _requiredFields = MutableStateFlow(emptyList<RequiredField>())
    val requiredFields = _requiredFields.asStateFlow()

    init {
        scope.launch {
            _authors.value = authorDao.getAll()
        }
        scope.launch {
            _publishers.value = publisherDao.getAll()
        }
        scope.launch {
            if (bookId != null) {
                _loadingState.value = LoadingState.LOADING
                val book = bookDao.getBookById(bookId)
                if (book != null) {
                    _title.value = book.title
                    _description.value = book.description
                    _author.value = authorDao.getById(book.authorId)
                    _publisher.value = publisherDao.getById(book.publisherId)
                    _publishYear.value = book.publishYear
                    _pages.value = book.pages
                    _imagePath.value = book.photoPath
                    _isEditMode.value = true
                    _loadingState.value = LoadingState.LOADED
                } else {
                    _objectLoadingError.value = LoadingObjectError.NOT_FOUND
                    _loadingState.value = LoadingState.ERROR
                }
            }
        }
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.Save -> {
                if (isEditMode.value) {
                    update(intent.context)
                } else
                    save(intent.context)
            }

            is Intent.Back -> navController.popBackStack()
            is Intent.ChangedAuthor -> {
                _author.value = intent.text
                _requiredFields.value = _requiredFields.value.minus(RequiredField.AUTHOR)

            }

            is Intent.ChangedDescription -> {
                _description.value = intent.text
                if (intent.text.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.DESCRIPTION)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.DESCRIPTION)
                }
            }

            is Intent.ChangedPublisher -> {
                _publisher.value = intent.text
                _requiredFields.value = _requiredFields.value.minus(RequiredField.PUBLISHER)
            }

            is Intent.ChangedTitle -> {
                _title.value = intent.text
                if (intent.text.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.TITLE)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.TITLE)
                }
            }

            is Intent.ChangedPublishYear -> {
                if (intent.text.all { it.isDigit() } && intent.text.length <= 4)
                    _publishYear.value = intent.text.toIntOrNull()
                if (intent.text.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.PUBLISH_YEAR)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.PUBLISH_YEAR)
                }
            }

            is Intent.SearchAuthor -> {
                scope.launch {
                    _authors.value = authorDao.search(intent.text)
                }
            }

            is Intent.SearchPublisher -> {
                scope.launch {
                    _publishers.value = publisherDao.search(intent.text)
                }
            }

            is Intent.ChangedPage -> {
                if (intent.text.all { it.isDigit() })
                    _pages.value = intent.text.toIntOrNull()
                if (intent.text.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.PAGES)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.PAGES)
                }
            }

            is Intent.ChangedPhoto -> {
                _imageUri.value = intent.uri
            }
        }
    }

    private fun update(context: Context) {
        scope.launch {
            _processState.value = ProcessState.LOADING
            val imageUri = imageUri.value
            val title = title.value
            val description = description.value
            val author = author.value
            val publisher = publisher.value
            val publishYear = publishYear.value
            val pages = pages.value
            val imagePath = imagePath.value
            if (author == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.AUTHOR)
                return@launch
            }
            if (publisher == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.PUBLISHER)
                return@launch
            }
            if (publishYear == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.PUBLISH_YEAR)
                return@launch
            }
            if (pages == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.PAGES)
                return@launch
            }
            if (imageUri != null) {
                println("Old path: $imagePath")
                val filePath = loadBitmapToFile(imageUri, context)
                println("File path: $filePath")
                if (filePath != null) {
                    bookDao.update(
                        Book(
                            id = bookId,
                            title = title,
                            description = description,
                            authorId = author.id!!,
                            publisherId = publisher.id!!,
                            publishYear = publishYear,
                            pages = pages,
                            photoPath = filePath
                        )
                    )
                }
            } else {
                bookDao.update(
                    Book(
                        id = bookId,
                        title = title,
                        description = description,
                        authorId = author.id!!,
                        publisherId = publisher.id!!,
                        publishYear = publishYear,
                        pages = pages,
                        photoPath = imagePath
                    )
                )
            }
            _processState.value = ProcessState.SUCCESS
        }
    }

    private fun save(context: Context) {
        scope.launch {
            _processState.value = ProcessState.LOADING
            val imageUri = imageUri.value
            val title = title.value
            val description = description.value
            val author = author.value
            val publisher = publisher.value
            val publishYear = publishYear.value
            val pages = pages.value
            if (author == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.AUTHOR)
                _processState.value = ProcessState.ERROR
                return@launch
            }
            if (publisher == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.PUBLISHER)
                _processState.value = ProcessState.ERROR
                return@launch
            }
            if (publishYear == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.PUBLISH_YEAR)
                _processState.value = ProcessState.ERROR
                return@launch
            }
            if (pages == null) {
                _requiredFields.value = requiredFields.value.plus(RequiredField.PAGES)
                _processState.value = ProcessState.ERROR
                return@launch
            }
            if (imageUri != null) {
                println("Start save image")
                val filePath = loadBitmapToFile(imageUri, context)
                if (filePath != null) {
                    println("Start save")
                    bookDao.insert(
                        Book(
                            title = title,
                            description = description,
                            authorId = author.id!!,
                            publisherId = publisher.id!!,
                            publishYear = publishYear,
                            pages = pages,
                            photoPath = filePath
                        )
                    )
                    println("Save completed")
                }
            } else {
                bookDao.insert(
                    Book(
                        title = title,
                        description = description,
                        authorId = author.id!!,
                        publisherId = publisher.id!!,
                        publishYear = publishYear,
                        pages = pages
                    )
                )
            }
            _processState.value = ProcessState.SUCCESS
        }
    }

    sealed class Intent() {
        data class Save(val context: Context) : Intent()
        data object Back : Intent()
        data class ChangedPublishYear(val text: String) : Intent()
        data class ChangedTitle(val text: String) : Intent()
        data class ChangedDescription(val text: String) : Intent()
        data class ChangedAuthor(val text: Author) : Intent()
        data class ChangedPublisher(val text: Publisher) : Intent()
        data class SearchAuthor(val text: String) : Intent()
        data class SearchPublisher(val text: String) : Intent()
        data class ChangedPhoto(val uri: Uri) : Intent()
        data class ChangedPage(val text: String) : Intent()


    }


    enum class RequiredField {
        TITLE,
        DESCRIPTION,
        AUTHOR,
        PUBLISHER,
        PUBLISH_YEAR,
        PAGES
    }
}

