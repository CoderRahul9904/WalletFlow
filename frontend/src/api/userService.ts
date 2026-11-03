import { apiClient, normaliseError } from './client';
import type {
  CreateUserRequest,
  RequestUser,
  SignUpResponse,
} from './types';

/** POST /api/v1/users */
export async function createUser(req: CreateUserRequest): Promise<SignUpResponse> {
  try {
    const { data } = await apiClient.post<SignUpResponse>('/api/v1/users', req);
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

/** GET /api/v1/users/{id} */
export async function getUserById(id: number): Promise<RequestUser> {
  try {
    const { data } = await apiClient.get<RequestUser>(`/api/v1/users/${id}`);
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}

/** GET /api/v1/users?email= */
export async function getUserByEmail(email: string): Promise<RequestUser> {
  try {
    const { data } = await apiClient.get<RequestUser>('/api/v1/users', {
      params: { email },
    });
    return data;
  } catch (err) {
    throw normaliseError(err);
  }
}
