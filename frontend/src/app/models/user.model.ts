export interface User {
  id: number;
  email: string;
  roles: { id: number; name: string }[];
}