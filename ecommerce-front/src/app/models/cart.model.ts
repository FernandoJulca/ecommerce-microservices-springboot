export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productPrice: number;
  quantity: number;
  subtotal: number;
  imageUrl: string;
}

export interface Cart {
  id: number;
  userEmail: string;
  items: CartItem[];
  total: number;
  totalItems: number;
}