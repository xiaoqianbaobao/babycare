// 用户相关类型
export interface User {
  id: string
  username: string
  email: string
  phone: string
  avatar?: string
  nickname: string
  city?: string
  role: 'PARENT' | 'ADMIN'
  createdAt: string
  updatedAt: string
}

// 家庭相关类型
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

// 宝宝相关类型
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

// 成长记录相关类型
export interface GrowthRecord {
  id: string
  babyId: string
  type: 'PHOTO' | 'VIDEO' | 'DIARY' | 'MILESTONE'
  title: string
  content?: string
  mediaUrls: string[]
  tags: string[]
  createdBy: string
  createdAt: string
  updatedAt: string
}

export interface Milestone {
  id: string
  babyId: string
  title: string
  description: string
  achievedAt: string
  category: 'MOTOR' | 'LANGUAGE' | 'COGNITIVE' | 'SOCIAL'
  photos: string[]
  createdAt: string
}

// AI育儿相关类型
export interface AIChat {
  id: string
  userId: string
  babyId?: string
  messages: ChatMessage[]
  topic?: string
  createdAt: string
  updatedAt: string
}

export interface ChatMessage {
  id: string
  role: 'USER' | 'ASSISTANT'
  content: string
  timestamp: string
}

export interface DevelopmentAssessment {
  id: string
  babyId: string
  ageInMonths: number
  areas: AssessmentArea[]
  overallScore: number
  recommendations: string[]
  assessedAt: string
  nextAssessmentDate: string
}

export interface AssessmentArea {
  name: 'GROSS_MOTOR' | 'FINE_MOTOR' | 'LANGUAGE' | 'COGNITIVE' | 'SOCIAL_EMOTIONAL'
  score: number
  maxScore: number
  skills: AssessmentSkill[]
}

export interface AssessmentSkill {
  name: string
  achieved: boolean
  expectedAgeRange: [number, number] // 预期达成年龄范围（月）
  description: string
}

// 教育规划相关类型
export interface EducationPlan {
  id: string
  babyId: string
  title: string
  description: string
  targetAgeRange: [number, number]
  activities: Activity[]
  progress: PlanProgress
  createdAt: string
  updatedAt: string
}

export interface Activity {
  id: string
  title: string
  description: string
  type: 'PLAY' | 'LEARNING' | 'EXERCISE' | 'ART' | 'MUSIC'
  duration: number // 分钟
  materials: string[]
  instructions: string[]
  completed: boolean
  completedAt?: string
}

export interface PlanProgress {
  totalActivities: number
  completedActivities: number
  progressPercentage: number
  lastActivity?: string
}

// 家庭协作相关类型
export interface FamilyPost {
  id: string
  familyId: string
  authorId: string
  authorName: string
  authorAvatar?: string
  content: string
  images: string[]
  videos: string[]
  likes: string[] // 点赞用户ID列表
  comments: Comment[]
  createdAt: string
  updatedAt: string
}

export interface Comment {
  id: string
  postId: string
  authorId: string
  authorName: string
  authorAvatar?: string
  content: string
  createdAt: string
}

export interface FamilyTask {
  id: string
  familyId: string
  title: string
  description: string
  assignedTo: string[]
  assignedBy: string
  dueDate?: string
  priority: 'LOW' | 'MEDIUM' | 'HIGH'
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED'
  category: 'FEEDING' | 'DIAPER' | 'BATH' | 'PLAY' | 'EDUCATION' | 'MEDICAL' | 'OTHER'
  completedAt?: string
  completedBy?: string
  createdAt: string
  updatedAt: string
}

// 专家咨询相关类型
export interface Expert {
  id: string
  name: string
  title: string
  specialties: string[]
  avatar: string
  rating: number
  reviewCount: number
  experience: number // 年
  price: {
    textConsultation: number
    voiceConsultation: number
    videoConsultation: number
  }
  availability: boolean
}

export interface Consultation {
  id: string
  expertId: string
  userId: string
  babyId?: string
  type: 'TEXT' | 'VOICE' | 'VIDEO'
  topic: string
  description: string
  status: 'PENDING' | 'ACCEPTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED'
  scheduledAt?: string
  startedAt?: string
  endedAt?: string
  messages: ConsultationMessage[]
  price: number
  rating?: number
  review?: string
  createdAt: string
  updatedAt: string
}

export interface ConsultationMessage {
  id: string
  consultationId: string
  senderId: string
  senderType: 'USER' | 'EXPERT'
  content: string
  type: 'TEXT' | 'VOICE' | 'VIDEO' | 'IMAGE' | 'FILE'
  mediaUrl?: string
  timestamp: string
}

// API响应类型
export interface ApiResponse<T = any> {
  success: boolean
  data?: T
  message?: string
  error?: string
  code?: number
}

export interface PaginatedResponse<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
  totalPages: number
}

// 表单类型
export interface LoginForm {
  email: string // This field actually accepts either username or email
  password: string
  remember?: boolean
}

export interface RegisterForm {
  username: string
  password: string
  confirmPassword: string
  email?: string
  phone?: string
  nickname?: string
  agreement?: boolean
}

export interface BabyForm {
  name: string
  gender: 'MALE' | 'FEMALE'
  birthday: string
  avatar?: File
}

export interface FamilyForm {
  name: string
}

// 路由类型
export interface RouteConfig {
  path: string
  component: React.ComponentType
  exact?: boolean
  protected?: boolean
  title?: string
  icon?: React.ReactNode
}