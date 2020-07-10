package com.example.httpexample.api

import com.example.httpexample.model.Book
import com.example.httpexample.ui.RecyclerFragment.Companion.BOOKS_URI
import com.example.httpexample.ui.RecyclerFragment.Companion.ENDPOINT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.POST
import retrofit2.http.Body

interface BooksApi {
    @GET(BOOKS_URI)
    suspend fun getBooksAndShowIt(): List<Book>

    @DELETE("$BOOKS_URI/{id}")
    suspend fun removeBook(@Path("id") id: Int)

    @Headers("Content-Type: application/json")
    @PUT("$BOOKS_URI/{id}")
    suspend fun changeBook(
        @Path("id") id: Int,
        @Body book: Book
    )


    @Headers("Content-Type: application/json")
    @POST(BOOKS_URI)
    suspend fun addBook(@Body book: Book)
}

object BooksApiImpl {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl(ENDPOINT)
        .build()

    private val booksApi = retrofit.create(BooksApi::class.java)

    // For work need implement moshi, moshi-kotlin-codegen and plugin kapt
    suspend fun getBooksAndShowIt(): List<Book> {
        return withContext(Dispatchers.IO) {
            booksApi.getBooksAndShowIt()
        }
    }

    suspend fun removeBook(id: Int) {
        withContext(Dispatchers.IO) {
            booksApi.removeBook(id)
        }
    }

    suspend fun changeBook(id: Int, bookTitle: String) {
        withContext(Dispatchers.IO) {
            val body = Book(bookTitle, id)
            booksApi.changeBook(id, body)
        }
    }

    suspend fun addBook(book: Book) {
        withContext(Dispatchers.IO) {
            booksApi.addBook(book)
        }
    }
}