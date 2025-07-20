# Perbaikan Sistem Order Kosan

## Masalah yang Diperbaiki

### 1. **Status Mismatch - Seller Terima Tidak Terjadi Apa-apa**
**Masalah:** Database menggunakan status `'approved'` tetapi kode menggunakan `'accepted'`

**Perbaikan:**
- Mengubah semua referensi `"accepted"` menjadi `"approved"` di `OrderCardController.java`
- Memperbarui logika tampilan button dan status berdasarkan `"approved"`

### 2. **User Tidak Bisa Memesan Kosan**
**Masalah:** Tidak ada validasi untuk mencegah pemesanan kosan yang sudah disewa atau user yang sudah memiliki pesanan pending

**Perbaikan:**
- Menambahkan method `hasActiveOrder()` di `OrderDAO.java` untuk mengecek apakah user sudah memiliki pesanan pending
- Menambahkan method `isKosanAvailable()` di `OrderDAO.java` untuk mengecek ketersediaan kosan
- Menambahkan validasi di `OrderKosController.java` sebelum membuat pesanan baru

### 3. **Integrasi dengan Tabel Sewa**
**Masalah:** Ketika seller menerima pesanan, tidak ada record yang dibuat di tabel `sewa`

**Perbaikan:**
- Menambahkan method `updateOrderStatusWithSewa()` di `OrderDAO.java` yang menggunakan transaction
- Ketika status diubah ke `'approved'`, otomatis membuat record di tabel `sewa`
- Menggunakan transaction untuk memastikan konsistensi data

### 4. **Filter Kosan yang Sudah Disewa**
**Masalah:** Kosan yang sudah disewa masih muncul di daftar untuk user lain

**Perbaikan:**
- Memperbarui query di `getAllKosan()` dan `filterKosan()` untuk mengecek status sewa
- Hanya menampilkan kosan yang belum memiliki order dengan status `'approved'`

## Detail Perubahan File

### 1. `OrderCardController.java`
```java
// Mengubah status dari "accepted" ke "approved"
private void handleAccept() {
    updateOrderStatus("approved");
}

// Memperbarui logika tampilan berdasarkan status "approved"
case "approved":
    statusLabel.getStyleClass().add("order-status-accepted");
    break;

// Menggunakan method baru dengan transaction
private void updateOrderStatus(String status) {
    OrderDAO orderDAO = new OrderDAO();
    boolean success = orderDAO.updateOrderStatusWithSewa(order.getId_order(), status);
    if (success && onOrderUpdate != null) {
        onOrderUpdate.run();
    }
}
```

### 2. `OrderDAO.java`
```java
// Method untuk mengecek apakah user sudah memiliki pesanan pending
public boolean hasActiveOrder(int kosanId, int userId) {
    String sql = "SELECT COUNT(*) FROM orders WHERE id_kos = ? AND id_user = ? AND status = 'pending'";
    // ... implementasi
}

// Method untuk mengecek ketersediaan kosan
public boolean isKosanAvailable(int kosanId) {
    String sql = "SELECT COUNT(*) FROM orders WHERE id_kos = ? AND status = 'approved'";
    // ... implementasi
}

// Method dengan transaction untuk update status dan create sewa
public boolean updateOrderStatusWithSewa(int orderId, String status) {
    // Menggunakan transaction untuk memastikan konsistensi
    // Jika status = "approved", otomatis create record di tabel sewa
}
```

### 3. `OrderKosController.java`
```java
// Validasi sebelum membuat pesanan
// Check if user already has a pending order for this kosan
if (orderDAO.hasActiveOrder(kosan.getId_kos(), currentUser.getId())) {
    showAlert("Error", "Anda sudah memiliki pesanan yang sedang menunggu konfirmasi untuk kosan ini.", Alert.AlertType.ERROR);
    return;
}

// Check if kosan is still available
if (!orderDAO.isKosanAvailable(kosan.getId_kos())) {
    showAlert("Error", "Maaf, kosan ini sudah tidak tersedia.", Alert.AlertType.ERROR);
    return;
}
```

### 4. `KosanDAO.java`
```java
// Query yang diperbarui untuk mengecek status sewa
String sql = "SELECT k.*, COALESCE(AVG(u.rating), 0) as rating, " +
             "(SELECT u.komentar FROM ulasan u WHERE u.id_kos = k.id_kos ORDER BY u.tanggal DESC LIMIT 1) as last_comment, " +
             "(SELECT COUNT(*) FROM orders o WHERE o.id_kos = k.id_kos AND o.status = 'approved') as is_rented " +
             "FROM kosan k LEFT JOIN ulasan u ON k.id_kos = u.id_kos GROUP BY k.id_kos";

// Filter hanya kosan yang tersedia
if (rs.getInt("is_rented") == 0) {
    kosanList.add(kosan);
}
```

## Hasil Perbaikan

1. ✅ **Seller dapat menerima pesanan dengan benar** - Status sekarang menggunakan `'approved'` yang sesuai dengan database
2. ✅ **User tidak dapat memesan kosan yang sudah disewa** - Ada validasi ketersediaan kosan
3. ✅ **User tidak dapat membuat pesanan duplikat** - Ada validasi pesanan pending
4. ✅ **Integrasi dengan tabel sewa** - Ketika pesanan diterima, otomatis membuat record sewa
5. ✅ **Kosan yang sudah disewa tidak muncul di daftar** - Filter otomatis berdasarkan status

## Cara Testing

1. **Test Seller Accept Order:**
   - Login sebagai seller
   - Lihat pesanan pending
   - Klik "Terima" - seharusnya status berubah ke "APPROVED"

2. **Test User Order Prevention:**
   - Login sebagai user
   - Coba pesan kosan yang sudah ada pesanan pending - seharusnya ditolak
   - Coba pesan kosan yang sudah disewa - seharusnya ditolak

3. **Test Kosan Availability:**
   - Setelah seller terima pesanan, kosan seharusnya tidak muncul di daftar untuk user lain

## Database Schema yang Digunakan
Berdasarkan `data_kos (5).sql`:
- Tabel `orders` dengan status: `enum('pending','approved','rejected','paid')`
- Tabel `sewa` untuk record penyewaan aktif
- Tabel `kosan` untuk data kosan
- Tabel `users` untuk data pengguna