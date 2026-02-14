package im.manus.atlas.util

object Constants {
    object API {
        const val BASE_URL = "https://joaovidaamazonlog.github.io/atlas/data/"
        const val PARTNERS_ENDPOINT = "dados_mapa.json"
        const val GEOJSON_ENDPOINT = "optimization_data.geojson"
    }

    object CACHE {
        const val CACHE_DURATION_MINUTES = 60
        const val DATABASE_NAME = "atlas_database"
    }

    object MAP {
        const val DEFAULT_ZOOM = 12.0
        const val DEFAULT_LAT = -23.5505
        const val DEFAULT_LON = -46.6333
    }
}
