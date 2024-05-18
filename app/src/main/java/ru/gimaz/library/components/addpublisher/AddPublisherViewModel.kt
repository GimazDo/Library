package ru.gimaz.library.components.addpublisher

import android.content.Context
import android.net.Uri
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.gimaz.library.db.Publisher
import ru.gimaz.library.db.PublisherDao
import ru.gimaz.library.enums.LoadingObjectError
import ru.gimaz.library.enums.LoadingState
import ru.gimaz.library.enums.ProcessState
import ru.gimaz.library.util.loadBitmapToFile

class AddPublisherViewModel(
    private val navController: NavController,
    private val publisherDao: PublisherDao,
    private val publisherId: Int? = null
) {
    private val _saveState = MutableStateFlow(ProcessState.IDLE)

    val saveState = _saveState.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _address = MutableStateFlow("")
    val address = _address.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _website = MutableStateFlow("")
    val website = _website.asStateFlow()

    private val _logo = MutableStateFlow<Uri?>(null)
    val logo = _logo.asStateFlow()

    private val _logoPath = MutableStateFlow<String?>(null)
    val logoPath = _logoPath.asStateFlow()

    private val _yearOfFoundation = MutableStateFlow<Int?>(null)
    val yearOfFoundation = _yearOfFoundation.asStateFlow()

    private val _requiredFields = MutableStateFlow<List<RequiredField>>(emptyList())
    val requiredFields = _requiredFields.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode = _isEditMode.asStateFlow()
    private val _loadingState = MutableStateFlow(LoadingState.LOADING)
    val loadingState = _loadingState.asStateFlow()

    private val _objectLoadingError = MutableStateFlow<LoadingObjectError?>(null)
    val objectLoadingError = _objectLoadingError.asStateFlow()

    init {
        scope.launch {
            if (publisherId != null) {
                _loadingState.value = LoadingState.LOADING
                _isEditMode.value = true
                val publisher = publisherDao.getById(publisherId)
                if (publisher != null) {
                    _name.value = publisher.name
                    _description.value = publisher.description
                    _address.value = publisher.address
                    _email.value = publisher.email
                    _website.value = publisher.website
                    _logoPath.value = publisher.logoPath
                    _yearOfFoundation.value = publisher.yearOfFoundation
                    _loadingState.value = LoadingState.LOADED
                } else {
                    _loadingState.value = LoadingState.ERROR
                    _objectLoadingError.value = LoadingObjectError.NOT_FOUND
                }
            }
        }
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.Back -> navController.popBackStack()
            is Intent.NameChanged -> {
                if (intent.name.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.NAME)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.NAME)
                }
                _name.value = intent.name
            }

            is Intent.Save -> {
                if (isEditMode.value)
                    update(intent.context)
                else
                    save(intent.context)
            }

            is Intent.AddressChanged -> {
                _address.value = intent.address
            }

            is Intent.DescriptionChanged -> {
                if (intent.description.isBlank()) {
                    _requiredFields.value = _requiredFields.value.plus(RequiredField.DESCRIPTION)
                } else {
                    _requiredFields.value = _requiredFields.value.minus(RequiredField.DESCRIPTION)
                }
                _description.value = intent.description
            }

            is Intent.YearOfFoundationChanged -> {
                if (intent.yearOfFoundation.isBlank()) {
                    _requiredFields.value =
                        _requiredFields.value.plus(RequiredField.YEAR_OF_FOUNDATION)
                } else {
                    _requiredFields.value =
                        _requiredFields.value.minus(RequiredField.YEAR_OF_FOUNDATION)
                }
                if (intent.yearOfFoundation.all { it.isDigit() } && intent.yearOfFoundation.length <= 4)
                    _yearOfFoundation.value = intent.yearOfFoundation.toIntOrNull()
            }

            is Intent.EmailChanged -> {
                _email.value = intent.email
            }

            is Intent.LogoChanged -> {
                _logo.value = intent.logo
            }

            is Intent.WebsiteChanged -> {
                _website.value = intent.website
            }
        }
    }

    private fun update(context: Context) {
        scope.launch {
            _saveState.value = ProcessState.LOADING
            val name = _name.value
            val description = _description.value
            val address = _address.value
            val email = _email.value
            val website = _website.value
            val logo = _logo.value
            val yearOfFoundation = _yearOfFoundation.value
            if (logo != null) {
                val filePath = loadBitmapToFile(logo, context)
                if (filePath != null) {
                    publisherDao.update(
                        Publisher(
                            id = publisherId,
                            name = name,
                            description = description,
                            address = address,
                            email = email,
                            website = website,
                            logoPath = filePath,
                            yearOfFoundation = yearOfFoundation!!
                        )
                    )
                }
            } else {
                publisherDao.update(
                    Publisher(
                        id = publisherId,
                        name = name,
                        description = description,
                        address = address,
                        email = email,
                        website = website,
                        yearOfFoundation = yearOfFoundation!!
                    )
                )
            }
            _saveState.value = ProcessState.SUCCESS
        }
    }


    private fun save(context: Context) {
        scope.launch {
            _saveState.value = ProcessState.LOADING
            val name = _name.value
            val description = _description.value
            val address = _address.value
            val email = _email.value
            val website = _website.value
            val logo = _logo.value
            val logoPath = _logoPath.value
            val yearOfFoundation = _yearOfFoundation.value
            if (logo != null) {
                val filePath = loadBitmapToFile(logo, context)
                if (filePath != null) {
                    publisherDao.insert(
                        Publisher(
                            name = name,
                            description = description,
                            address = address,
                            email = email,
                            website = website,
                            logoPath = filePath,
                            yearOfFoundation = yearOfFoundation!!
                        )
                    )
                }
            } else {
                publisherDao.insert(
                    Publisher(
                        name = name,
                        description = description,
                        address = address,
                        email = email,
                        website = website,
                        yearOfFoundation = yearOfFoundation!!,
                        logoPath = logoPath
                    )
                )
            }
            _saveState.value = ProcessState.SUCCESS
        }
    }

    sealed class Intent {
        data object Back : Intent()
        data class NameChanged(val name: String) : Intent()
        data class DescriptionChanged(val description: String) : Intent()
        data class AddressChanged(val address: String) : Intent()
        data class EmailChanged(val email: String) : Intent()
        data class WebsiteChanged(val website: String) : Intent()
        data class LogoChanged(val logo: Uri) : Intent()
        data class YearOfFoundationChanged(val yearOfFoundation: String) : Intent()
        data class Save(val context: Context) : Intent()
    }

    enum class RequiredField {
        NAME,
        DESCRIPTION,
        YEAR_OF_FOUNDATION,
    }
}