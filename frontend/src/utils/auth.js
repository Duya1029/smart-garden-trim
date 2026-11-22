// token 的本地持久化存取（localStorage）
const TOKEN_KEY = 'smart_garden_token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY) || ''
}

export function setToken(token) {
  if (token) {
    localStorage.setItem(TOKEN_KEY, token)
  }
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}
