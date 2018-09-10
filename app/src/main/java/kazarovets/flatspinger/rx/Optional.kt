package kazarovets.flatspinger.rx

class Optional<T> private constructor(val value: T? = null) {

    interface Action<T> {
        fun apply(value: T)
    }



    fun ifPresent(action: Action<T>) {
        if (value != null) {
            action.apply(value)
        }
    }

    companion object {

        fun <T> empty(): Optional<T> {
            return Optional()
        }

        fun <T> of(value: T): Optional<T> {
            return Optional(value)
        }
    }

}