# Receipt Viewing Feature Implementation

## Overview
Implemented a feature that allows users to view their purchase receipts on the user dashboard after orders are approved by sellers.

## Changes Made

### 1. UserDashboard.fxml
- Added a new "Struk Belanja" (Receipt) button to the sidebar navigation
- Button is positioned between "Sewa Kosan" and "Profil" buttons

### 2. UserDashboardController.java
- Added `struksButton` FXML field for the new button
- Implemented `handleStruksClick()` method to show approved orders
- Added `updateButtonStyles()` method to manage sidebar button selection states
- Added `showApprovedOrders()` method to load and display approved orders
- Created `ApprovedOrderCell` inner class for custom ListView cell rendering
- Added `createOrderCard()` method to create order cards with receipt viewing buttons
- Added `showReceipt()` method to open receipt window for selected orders
- Updated all button handlers to use the new button styling system
- Added proper error handling and user session validation

### 3. mamakos_styles.css
- Added styles for approved orders display:
  - `.order-id-label` - Order ID styling
  - `.kosan-name-label` - Kosan name styling
  - `.date-label` - Date display styling
  - `.status-approved-label` - Approved status badge styling
  - `.view-receipt-button` - Receipt viewing button styling
  - `.no-data-label` - No data message styling
  - `.error-label` - Error message styling
  - `.receipt-footer-status` - Receipt status footer styling
  - `.receipt-label-total` - Receipt total label styling

## Features Implemented

### 1. Sidebar Navigation
- Added "Struk Belanja" button to user dashboard sidebar
- Implemented proper button selection states with visual feedback
- All sidebar buttons now properly update their active state

### 2. Approved Orders Display
- Shows only orders with "accepted" status
- Displays order information in card format:
  - Order ID (formatted as ORD######)
  - Kosan name
  - Order date
  - Status (DITERIMA)
  - "Lihat Struk" button

### 3. Receipt Viewing
- Opens receipt in a new window when "Lihat Struk" button is clicked
- Retrieves complete kosan details for accurate receipt display
- Uses existing ReceiptController for consistent receipt formatting
- Receipt window is resizable with proper minimum dimensions

### 4. Error Handling
- Validates user session before loading orders
- Handles database connection errors gracefully
- Shows appropriate messages for empty order lists
- Proper exception handling for receipt window creation

## Technical Details

### Database Integration
- Uses existing `OrderDAO.getOrdersByUserIdWithKosan()` method
- Filters orders by "accepted" status
- Retrieves kosan details using `KosanDAO.getKosanById()`

### Session Management
- Uses `SessionManager.getInstance().getCurrentUser().getId()` to get current user ID
- Validates user session before processing requests

### UI Components
- Custom ListView with ApprovedOrderCell for order display
- VBox-based order cards with proper styling
- HBox layout for button positioning
- Proper spacing and padding for visual appeal

## Usage Instructions

1. **Access Receipt View**: Click "Struk Belanja" button in the user dashboard sidebar
2. **View Orders**: See list of all approved orders with basic information
3. **Open Receipt**: Click "Lihat Struk" button on any order card to view detailed receipt
4. **Receipt Actions**: In receipt window, users can:
   - Print the receipt
   - View detailed order information
   - Return to dashboard
   - Close the receipt window

## Benefits

1. **User Convenience**: Users can easily access their purchase receipts anytime
2. **Order Tracking**: Clear visibility of approved orders and their status
3. **Professional Documentation**: Properly formatted receipts for record keeping
4. **Seamless Integration**: Uses existing receipt functionality and styling
5. **Responsive Design**: Works with the existing responsive UI framework

## Future Enhancements

1. **Search and Filter**: Add search functionality for orders by date or kosan name
2. **Export Options**: Add PDF export functionality for receipts
3. **Order History**: Include rejected and pending orders with different status indicators
4. **Bulk Actions**: Allow users to print multiple receipts at once
5. **Email Integration**: Send receipts via email functionality