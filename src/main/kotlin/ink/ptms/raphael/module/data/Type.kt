package ink.ptms.raphael.module.data

import ink.ptms.raphael.Raphael

/**
 * Chemdah
 * ink.ptms.chemdah.database.Type
 *
 * @author sky
 * @since 2021/3/5 3:51 下午
 */
enum class Type {

    LOCAL, SQL;

    companion object {

        val INSTANCE: Type by lazy {
            try {
                valueOf(Raphael.conf.getString("Database.use", "")!!.toUpperCase())
            } catch (ignored: Throwable) {
                LOCAL
            }
        }
    }
}