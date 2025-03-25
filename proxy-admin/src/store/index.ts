import {createPinia, type Pinia} from 'pinia'
import type {App} from 'vue';

const store: Pinia = createPinia()

export function setupStore(app: App<Element>) {
  app.use(store)
}

export {store}