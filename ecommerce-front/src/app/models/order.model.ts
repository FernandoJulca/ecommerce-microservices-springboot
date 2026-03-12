export interface CreateOrderRequest {
  shippingAddress: string;
}

export interface OrderItem {
  id: number;
  productName: string;
  quantity: number;
  productPrice: number;
  subtotal: number;
  imageUrl: string;
}

export interface Order {
  id: number;
  userEmail: string;
  status: string;
  total: number;
  shippingAddress: string;
  items: OrderItem[];
  createdAt: string;
}