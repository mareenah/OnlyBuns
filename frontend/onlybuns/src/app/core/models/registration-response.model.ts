import { User } from './user.model';

export interface RegistrationResponse {
  id: number;
  username: string;
  password: string;
  role: string;
  name: string;
  surname: string;
  user: User;
  email: string;
}
