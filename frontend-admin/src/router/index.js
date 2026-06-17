import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue')
      },
      {
        path: '/courses',
        name: 'CourseManagement',
        component: () => import('../views/CourseManagement.vue')
      },
      {
        path: '/courses/create',
        name: 'CreateCourse',
        component: () => import('../views/CreateCourse.vue')
      },
      {
        path: '/courses/edit/:id',
        name: 'EditCourse',
        component: () => import('../views/EditCourse.vue')
      },
      {
        path: '/users',
        name: 'UserManagement',
        component: () => import('../views/UserManagement.vue')
      },
      {
        path: '/users/detail/:id',
        name: 'UserDetail',
        component: () => import('../views/UserDetail.vue')
      },
      {
        path: '/achievements',
        name: 'AchievementManagement',
        component: () => import('../views/AchievementManagement.vue')
      },
      {
        path: '/posts',
        name: 'PostManagement',
        component: () => import('../views/PostManagement.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('token')
  
  if (to.name !== 'Login' && !isLoggedIn) {
    next({ name: 'Login' })
  } else {
    next()
  }
})

export default router