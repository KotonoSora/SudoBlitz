package com.kotonosora.sudoblitz.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kotonosora.sudoblitz.billing.BillingManager
import com.kotonosora.sudoblitz.billing.StoreProduct
import com.kotonosora.sudoblitz.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ShopViewModel(
    application: Application,
    preferencesRepository: UserPreferencesRepository
) : AndroidViewModel(application) {

    private val billingManager = BillingManager(application, preferencesRepository)

    val products: StateFlow<List<StoreProduct>> = billingManager.products
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coins: StateFlow<Int> = preferencesRepository.coinsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun buyProduct(activity: Activity, product: StoreProduct) {
        billingManager.launchBillingFlow(activity, product)
    }

    override fun onCleared() {
        super.onCleared()
        billingManager.endConnection()
    }

    companion object {
        fun provideFactory(
            application: Application,
            repository: UserPreferencesRepository
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(ShopViewModel::class.java)) {
                        return ShopViewModel(application, repository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
    }
}
