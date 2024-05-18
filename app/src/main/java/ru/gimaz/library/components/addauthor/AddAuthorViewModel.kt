package ru.gimaz.library.components.addauthor

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
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState
import ru.gimaz.library.enums.ProcessState
import ru.gimaz.library.util.loadBitmapToFile
import java.time.LocalDate


class AddAuthorViewModel(
    private val navigationController: NavController,
    private val authorDao: AuthorDao,
    private val authorId: Int? = null
) {

    private val _firstname = MutableStateFlow("")
    val firstname = _firstname.asStateFlow()

    private val _lastname = MutableStateFlow("")
    val lastname = _lastname.asStateFlow()

    private val _surname = MutableStateFlow("")
    val surname = _surname.asStateFlow()

    private val _dateOfBirthday = MutableStateFlow<LocalDate?>(null)
    val dateOfBirthday = _dateOfBirthday.asStateFlow()

    private val _biography = MutableStateFlow("")
    val biography = _biography.asStateFlow()

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri = _imageUri.asStateFlow()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath = _imagePath.asStateFlow()


    private val _saveState = MutableStateFlow(ProcessState.IDLE)

    val saveState = _saveState.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

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
            try {
                if (authorId != null) {
                    _loadingState.value = LoadingState.LOADING
                    _isEditMode.value = true
                    val author = authorDao.getById(authorId)
                    if (author != null) {
                        _firstname.value = author.firstName
                        _lastname.value = author.lastName
                        _surname.value = author.surname
                        _dateOfBirthday.value = author.dateOfBirth
                        _biography.value = author.biography
                        _imagePath.value = author.photoPath
                        _loadingState.value = LoadingState.LOADED
                    } else {
                        _objectLoadingError.value = LoadingObjectError.NOT_FOUND
                        _loadingState.value = LoadingState.ERROR
                    }
                }
            } catch (e: Exception) {
                _objectLoadingError.value = LoadingObjectError.INTERNAL_ERROR
                _loadingState.value = LoadingState.ERROR
            }
        }
    }

    fun handleIntent(intent: Intent) {
        when (intent) {

            is Intent.ChangePhoto -> {
                _imageUri.value = intent.uri
            }

            is Intent.Save -> {
                if (isEditMode.value) {
                    update(intent.context)
                } else {
                    save(intent.context)
                }
            }

            Intent.Back -> {
                navigationController.popBackStack()
            }

            is Intent.ChangeFirstname -> {
                _firstname.value = intent.name
                if (intent.name.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.FIRSTNAME)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.FIRSTNAME)
                }
            }

            is Intent.ChangeLastname -> {
                _lastname.value = intent.name
                if (intent.name.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.LASTNAME)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.LASTNAME)
                }
            }

            is Intent.ChangeSurname -> {
                _surname.value = intent.name

            }

            is Intent.ChangeDateBirthday -> {
                _dateOfBirthday.value = intent.date
                if (intent.date == null) {
                    _requiredFields.value =
                        _requiredFields.value.plus(RequiredField.DATE_OF_BIRTHDAY)
                } else {
                    _requiredFields.value =
                        _requiredFields.value.minus(RequiredField.DATE_OF_BIRTHDAY)
                }
            }

            is Intent.ChangeBiography -> {
                _biography.value = intent.text
            }
        }
    }

    private fun save(context: Context) {
        scope.launch {
            _saveState.value = ProcessState.LOADING
            val imageUri = _imageUri.value
            val firstname = _firstname.value
            val surname = _surname.value
            val lastname = _lastname.value
            val biography = _biography.value
            val dateOfBirthday = _dateOfBirthday.value ?: return@launch
            if (imageUri != null) {
                val filePath = loadBitmapToFile(imageUri, context)
                if (filePath != null) {
                    authorDao.insert(
                        Author(
                            firstName = firstname,
                            lastName = lastname,
                            surname = surname,
                            dateOfBirth = dateOfBirthday,
                            photoPath = filePath,
                            biography = biography
                        )
                    )
                    _saveState.value = ProcessState.SUCCESS
                }
            } else {
                authorDao.insert(
                    Author(
                        firstName = firstname,
                        lastName = lastname,
                        surname = surname,
                        dateOfBirth = dateOfBirthday,
                        biography = biography
                    )
                )
                _saveState.value = ProcessState.SUCCESS
            }
        }
    }

    private fun update(context: Context) {
        scope.launch {
            _saveState.value = ProcessState.LOADING
            val imageUri = _imageUri.value
            val firstname = _firstname.value
            val surname = _surname.value
            val lastname = _lastname.value
            val biography = _biography.value
            val imagePath = _imagePath.value
            val dateOfBirthday = _dateOfBirthday.value!!
            if (imageUri != null) {
                val filePath = loadBitmapToFile(imageUri, context)
                if (filePath != null) {

                    authorDao.update(
                        Author(
                            id = authorId!!,
                            firstName = firstname,
                            lastName = lastname,
                            surname = surname,
                            dateOfBirth = dateOfBirthday,
                            photoPath = filePath,
                            biography = biography
                        )
                    )
                    _saveState.value = ProcessState.SUCCESS
                }
            } else {
                authorDao.update(
                    Author(
                        id = authorId!!,
                        firstName = firstname,
                        lastName = lastname,
                        surname = surname,
                        dateOfBirth = dateOfBirthday,
                        biography = biography,
                        photoPath = imagePath
                    )
                )
                _saveState.value = ProcessState.SUCCESS
            }
        }
    }

    sealed class
    Intent {
        data class ChangeFirstname(val name: String) : Intent()
        data class ChangeLastname(val name: String) : Intent()
        data class ChangeSurname(val name: String) : Intent()
        data class ChangeDateBirthday(val date: LocalDate?) : Intent()
        data class ChangePhoto(val uri: Uri) : Intent()
        data class ChangeBiography(val text: String) : Intent()
        data class Save(val context: Context) : Intent()
        data object Back : Intent()
    }

    enum class RequiredField {
        FIRSTNAME,
        LASTNAME,
        DATE_OF_BIRTHDAY,
    }
}


