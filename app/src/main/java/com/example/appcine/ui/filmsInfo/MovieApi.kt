package com.example.appcine.ui.filmsInfo

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MovieApi {
    companion object {
        private val client = OkHttpClient()

        fun getMovieBackdrop(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val backdropPath = jsonObject.optString("backdrop_path")
                            callback(backdropPath)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun getMoviePoster(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val backdropPath = jsonObject.optString("poster_path")
                            callback(backdropPath)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun getMovieTitle(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val title = jsonObject.optString("title")
                            callback(title)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun getMovieOriginalTitle(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val originalTitle = jsonObject.optString("original_title")
                            callback(originalTitle)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun getDirector(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId/credits?api_key=$apiKey")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val crewArray = jsonObject.getJSONArray("crew")
                            for (i in 0 until crewArray.length()) {
                                val crewObject = crewArray.getJSONObject(i)
                                if (crewObject.optString("job") == "Director") {
                                    val directorName = crewObject.optString("name")
                                    callback(directorName)
                                    return
                                }
                            }
                        }
                        callback(null)
                    }
                }
            })
        }

        fun getReleaseYear(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val releaseDate = jsonObject.optString("release_date")
                            val releaseYear = extractYearFromDateString(releaseDate)
                            callback(releaseYear)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun extractYearFromDateString(dateString: String): String? {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.parse(dateString)
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.get(Calendar.YEAR).toString()
            } catch (e: Exception) {
                null
            }
        }

        fun getRuntime(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val runtime = jsonObject.optString("runtime")
                            callback(runtime)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun getTagline(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val tagline = jsonObject.optString("tagline").toUpperCase()
                            callback(tagline)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun getOverview(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val overview = jsonObject.optString("overview")
                            callback(overview)
                        } ?: run {
                            callback(null)
                        }
                    }
                }
            })
        }

        fun getOfficialTrailer(movieId: Int, apiKey: String, callback: (String?) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId/videos")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(null)
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val resultsArray = jsonObject.getJSONArray("results")

                            // Buscar el trailer oficial en los resultados
                            for (i in 0 until resultsArray.length()) {
                                val resultObject = resultsArray.getJSONObject(i)
                                if (resultObject.optString("name") == "Official Trailer") {
                                    val trailerKey = resultObject.optString("key")
                                    callback(trailerKey)
                                    return
                                }
                            }
                        }
                        callback(null) // No se encontró el trailer oficial
                    }
                }
            })
        }

        fun getProviders(movieId: Int, apiKey: String, callback: (List<Pair<String, String>>) -> Unit) {
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/$movieId/watch/providers")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", "Bearer $apiKey")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback(emptyList())
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            callback(emptyList())
                            return
                        }

                        val responseData = response.body()?.string()
                        responseData?.let {
                            val jsonObject = JSONObject(it)
                            val resultsObject = jsonObject.optJSONObject("results")
                            val usProvidersObject = resultsObject?.optJSONObject("US")

                            val providers = mutableListOf<Pair<String, String>>()

                            // Obtener los proveedores de US si existen
                            usProvidersObject?.let { usProviders ->
                                usProviders.optJSONArray("flatrate")?.let { flatrateArray ->
                                    for (i in 0 until flatrateArray.length()) {
                                        val providerObject = flatrateArray.getJSONObject(i)
                                        val providerName = providerObject.optString("provider_name")
                                        val logoPath = providerObject.optString("logo_path")
                                        providers.add(Pair(providerName, logoPath))
                                    }
                                }

                                usProviders.optJSONArray("buy")?.let { buyArray ->
                                    for (i in 0 until buyArray.length()) {
                                        val providerObject = buyArray.getJSONObject(i)
                                        val providerName = providerObject.optString("provider_name")
                                        val logoPath = providerObject.optString("logo_path")
                                        providers.add(Pair(providerName, logoPath))
                                    }
                                }

                                usProviders.optJSONArray("rent")?.let { rentArray ->
                                    for (i in 0 until rentArray.length()) {
                                        val providerObject = rentArray.getJSONObject(i)
                                        val providerName = providerObject.optString("provider_name")
                                        val logoPath = providerObject.optString("logo_path")
                                        providers.add(Pair(providerName, logoPath))
                                    }
                                }
                            }

                            callback(providers)
                        } ?: run {
                            callback(emptyList())
                        }
                    }
                }
            })
        }

    }
}
