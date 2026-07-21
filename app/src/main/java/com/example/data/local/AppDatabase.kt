package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.models.UserEntity
import com.example.data.models.BookingEntity
import com.example.data.models.ProductEntity
import com.example.data.models.ChatEntity
import com.example.data.models.AddressEntity
import com.example.data.models.FavoriteTechnicianEntity
import com.example.data.models.SavedPaymentMethodEntity
import com.example.data.models.WishlistEntity
import com.example.data.models.ReferralEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings ORDER BY timestamp DESC")
    suspend fun getAllBookingsSync(): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: Int): Flow<BookingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    suspend fun updateBookingStatus(id: Int, status: String)

    @Query("DELETE FROM bookings WHERE id = :id")
    suspend fun deleteBookingById(id: Int)
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats WHERE bookingId = :bookingId ORDER BY timestamp ASC")
    fun getChatsForBooking(bookingId: Int): Flow<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity): Long

    @Query("DELETE FROM chats WHERE bookingId = :bookingId")
    suspend fun clearChatsForBooking(bookingId: Int)
}

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses")
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Query("DELETE FROM addresses WHERE id = :id")
    suspend fun deleteAddress(id: Int)
}

@Dao
interface FavoriteTechnicianDao {
    @Query("SELECT * FROM favorite_technicians")
    fun getAllFavoriteTechnicians(): Flow<List<FavoriteTechnicianEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(tech: FavoriteTechnicianEntity): Long

    @Query("DELETE FROM favorite_technicians WHERE id = :id")
    suspend fun deleteFavorite(id: Int)
}

@Dao
interface SavedPaymentMethodDao {
    @Query("SELECT * FROM saved_payments")
    fun getAllPayments(): Flow<List<SavedPaymentMethodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: SavedPaymentMethodEntity): Long

    @Query("DELETE FROM saved_payments WHERE id = :id")
    suspend fun deletePayment(id: Int)
}

@Dao
interface WishlistDao {
    @Query("SELECT * FROM wishlist")
    fun getWishlist(): Flow<List<WishlistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWishlist(item: WishlistEntity): Long

    @Query("DELETE FROM wishlist WHERE id = :id")
    suspend fun removeFromWishlist(id: Int)
    
    @Query("DELETE FROM wishlist WHERE serviceName = :serviceName")
    suspend fun removeFromWishlistByName(serviceName: String)
}

@Dao
interface ReferralDao {
    @Query("SELECT * FROM referrals")
    fun getAllReferrals(): Flow<List<ReferralEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(ref: ReferralEntity): Long
}

@Database(
    entities = [
        UserEntity::class,
        BookingEntity::class,
        ProductEntity::class,
        ChatEntity::class,
        AddressEntity::class,
        FavoriteTechnicianEntity::class,
        SavedPaymentMethodEntity::class,
        WishlistEntity::class,
        ReferralEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookingDao(): BookingDao
    abstract fun productDao(): ProductDao
    abstract fun chatDao(): ChatDao
    abstract fun addressDao(): AddressDao
    abstract fun favoriteTechnicianDao(): FavoriteTechnicianDao
    abstract fun savedPaymentMethodDao(): SavedPaymentMethodDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun referralDao(): ReferralDao
}
