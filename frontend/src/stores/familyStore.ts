import { create } from 'zustand'

export interface Baby {
  id: string
  name: string
  gender: 'MALE' | 'FEMALE'
  birthday: string
  avatar?: string
  familyId: string
  createdAt: string
  updatedAt: string
}

export interface Family {
  id: string
  name: string
  inviteCode: string
  babies: Baby[]
  members: FamilyMember[]
  createdAt: string
  updatedAt: string
}

export interface FamilyMember {
  id: string
  userId: string
  familyId: string
  role: 'CREATOR' | 'PARENT' | 'GRANDPARENT' | 'RELATIVE'
  nickname: string
  avatar?: string
  joinedAt: string
}

interface FamilyState {
  currentFamily: Family | null
  currentBaby: Baby | null
  families: Family[]
  isLoading: boolean
  error: string | null

  // Actions
  createFamily: (familyData: { name: string }) => Promise<void>
  joinFamily: (inviteCode: string) => Promise<void>
  switchFamily: (familyId: string) => void
  addBaby: (babyData: Omit<Baby, 'id' | 'familyId' | 'createdAt' | 'updatedAt'>) => Promise<void>
  switchBaby: (babyId: string) => void
  updateBaby: (babyId: string, babyData: Partial<Baby>) => Promise<void>
  inviteMember: (email: string, role: FamilyMember['role']) => Promise<void>
  loadFamilies: () => Promise<void>
  setLoading: (loading: boolean) => void
  setError: (error: string | null) => void
}

export const useFamilyStore = create<FamilyState>((set, get) => ({
  currentFamily: null,
  currentBaby: null,
  families: [],
  isLoading: false,
  error: null,

  createFamily: async (familyData) => {
    set({ isLoading: true, error: null })
    
    try {
      // TODO: 替换为实际的API调用
      const response = await fetch('/api/families', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(familyData),
      })

      if (!response.ok) {
        throw new Error('创建家庭失败')
      }

      const newFamily = await response.json()
      
      set(state => ({
        families: [...state.families, newFamily],
        currentFamily: newFamily,
        isLoading: false,
      }))
    } catch (error) {
      set({
        isLoading: false,
        error: error instanceof Error ? error.message : '创建家庭失败',
      })
    }
  },

  joinFamily: async (inviteCode) => {
    set({ isLoading: true, error: null })
    
    try {
      // TODO: 替换为实际的API调用
      const response = await fetch('/api/families/join', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({ inviteCode }),
      })

      if (!response.ok) {
        throw new Error('加入家庭失败')
      }

      const family = await response.json()
      
      set(state => ({
        families: [...state.families, family],
        currentFamily: family,
        isLoading: false,
      }))
    } catch (error) {
      set({
        isLoading: false,
        error: error instanceof Error ? error.message : '加入家庭失败',
      })
    }
  },

  switchFamily: (familyId) => {
    const families = get().families
    const family = families.find(f => f.id === familyId)
    
    if (family) {
      set({
        currentFamily: family,
        currentBaby: family.babies[0] || null,
      })
    }
  },

  addBaby: async (babyData) => {
    set({ isLoading: true, error: null })
    
    try {
      const currentFamily = get().currentFamily
      if (!currentFamily) {
        throw new Error('请先选择家庭')
      }

      // TODO: 替换为实际的API调用
      const response = await fetch('/api/babies', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          ...babyData,
          familyId: currentFamily.id,
        }),
      })

      if (!response.ok) {
        throw new Error('添加宝宝失败')
      }

      const newBaby = await response.json()
      
      set(state => ({
        currentFamily: state.currentFamily ? {
          ...state.currentFamily,
          babies: [...state.currentFamily.babies, newBaby],
        } : null,
        currentBaby: newBaby,
        isLoading: false,
      }))
    } catch (error) {
      set({
        isLoading: false,
        error: error instanceof Error ? error.message : '添加宝宝失败',
      })
    }
  },

  switchBaby: (babyId) => {
    const currentFamily = get().currentFamily
    if (currentFamily) {
      const baby = currentFamily.babies.find(b => b.id === babyId)
      if (baby) {
        set({ currentBaby: baby })
      }
    }
  },

  updateBaby: async (babyId, babyData) => {
    set({ isLoading: true, error: null })
    
    try {
      // TODO: 替换为实际的API调用
      const response = await fetch(`/api/babies/${babyId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify(babyData),
      })

      if (!response.ok) {
        throw new Error('更新宝宝信息失败')
      }

      const updatedBaby = await response.json()
      
      set(state => ({
        currentFamily: state.currentFamily ? {
          ...state.currentFamily,
          babies: state.currentFamily.babies.map(baby =>
            baby.id === babyId ? updatedBaby : baby
          ),
        } : null,
        currentBaby: state.currentBaby?.id === babyId ? updatedBaby : state.currentBaby,
        isLoading: false,
      }))
    } catch (error) {
      set({
        isLoading: false,
        error: error instanceof Error ? error.message : '更新宝宝信息失败',
      })
    }
  },

  inviteMember: async (email, role) => {
    set({ isLoading: true, error: null })
    
    try {
      const currentFamily = get().currentFamily
      if (!currentFamily) {
        throw new Error('请先选择家庭')
      }

      // TODO: 替换为实际的API调用
      const response = await fetch('/api/families/invite', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
        body: JSON.stringify({
          familyId: currentFamily.id,
          email,
          role,
        }),
      })

      if (!response.ok) {
        throw new Error('邀请家庭成员失败')
      }

      set({ isLoading: false })
    } catch (error) {
      set({
        isLoading: false,
        error: error instanceof Error ? error.message : '邀请家庭成员失败',
      })
    }
  },

  loadFamilies: async () => {
    set({ isLoading: true, error: null })
    
    try {
      // TODO: 替换为实际的API调用
      const response = await fetch('/api/families', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
        },
      })

      if (!response.ok) {
        throw new Error('加载家庭列表失败')
      }

      const families = await response.json()
      
      set({
        families,
        currentFamily: families[0] || null,
        currentBaby: families[0]?.babies[0] || null,
        isLoading: false,
      })
    } catch (error) {
      set({
        isLoading: false,
        error: error instanceof Error ? error.message : '加载家庭列表失败',
      })
    }
  },

  setLoading: (loading) => set({ isLoading: loading }),
  
  setError: (error) => set({ error }),
}))