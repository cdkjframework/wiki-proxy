interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_API_BASE_LOGIN_URL: string
  // more env variables...
}
interface ImportMeta {
  readonly env: ImportMetaEnv
}
