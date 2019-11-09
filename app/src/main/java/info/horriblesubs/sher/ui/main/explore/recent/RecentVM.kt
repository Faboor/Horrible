package info.horriblesubs.sher.ui.main.explore.recent

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.horriblesubs.sher.api.horrible.Horrible
import info.horriblesubs.sher.api.horrible.model.ItemRecent
import info.horriblesubs.sher.api.horrible.result.Result
import info.horriblesubs.sher.api.horrible.result.isNull
import info.horriblesubs.sher.common.LoadingListener
import info.horriblesubs.sher.common.Timer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentVM: ViewModel(), Timer.TimerData {
    internal val result = MutableLiveData<Result<ItemRecent>?>()
    override val time = MutableLiveData<String?>()
    private var listener: LoadingListener? = null
    private val timer = Timer(this)
    private var job: Job? = null

    private suspend fun fetchOnIO(job: Job, reset: Boolean): Result<ItemRecent>? {
        this.job = Job()
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO + job) {
            Horrible.service.recent(if(reset) "reset" else "0")
        }
    }

    internal fun initialize(listener: LoadingListener) {
        this.listener = listener
    }

    internal fun refresh(reset: Boolean = false) {
        if (result.value.isNull() || reset)
            viewModelScope.launch(Dispatchers.Main) {
                listener?.start()
                result.value = fetchOnIO(job?:Job(), reset)
                listener?.stop()
                timer.start()
            }
    }

    override fun timerEvent() {
        if (result.value.isNull()) time.postValue("Never")
        else time.postValue(result.value?.timePassed())
    }

    internal fun stop() {
        listener?.stop()
        job?.cancel()
        timer.stop()
        job = null
    }
}