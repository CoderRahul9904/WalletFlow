import axios, { AxiosError, type AxiosInstance } from 'axios';
import type { ApiErrorResponse } from './types';

/**
 * Centralised Axios instance. Base URL comes from VITE_API_BASE_URL
 * (defaults to http://localhost:8081 to match the Spring Boot dev server).
 */
export const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8081',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15_000,
});

/** A normalised error shape every service function throws. */
export class ApiError extends Error {
  status?: number;
  path?: string;
  raw?: unknown;

  constructor(message: string, status?: number, path?: string, raw?: unknown) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.path = path;
    this.raw = raw;
  }
}

/**
 * Convert any Axios error into an ApiError carrying the backend's
 * {message,status,path} body when available. Falls back to a network
 * message when no response was received.
 */
export function normaliseError(err: unknown): ApiError {
  if (err instanceof AxiosError) {
    const body = err.response?.data as ApiErrorResponse | undefined;
    const message =
      body?.message ??
      err.message ??
      'Unexpected network error — is the backend running?';
    return new ApiError(
      message,
      err.response?.status,
      body?.path ?? err.config?.url,
      err.response?.data ?? err,
    );
  }
  if (err instanceof Error) return new ApiError(err.message);
  return new ApiError(String(err));
}
