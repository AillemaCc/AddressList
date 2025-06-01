import {stuInstance} from './request'
import { useStuInfoStore } from '@/stores/stuInfo'

let stuPromise
export async function stuRefreshToken(){
    if(stuPromise) return stuPromise
    stuPromise = new Promise(async (resolve) => { 
        const stuInfoStore = useStuInfoStore()
        const resp = await stuInstance({
            url:'/api/stu/refreshToken',
            method:'post',
            data:{
                studentId:stuInfoStore.stuInfo.studentId,
                refreshToken:stuInfoStore.stuInfo.refreshToken
            },
            __isRefreshToken :true,
        })
        resolve(resp.data.code === '0')
    })
    stuPromise.finally(()=>{
        stuPromise = null
    })
    return stuPromise
}

export function stuIsRefreshRequest(config){
    return !!config.__isRefreshToken
}

import {adminInstance} from './request'
import { useAdminInfoStore } from '@/stores/adminInfo'

let adminPromise
export async function adminRefreshToken(){
    if(adminPromise) return adminPromise
    adminPromise = new Promise(async (resolve) => { 
        const adminInfoStore = useAdminInfoStore()
        const resp = await adminInstance({
            url:'/api/admin/refreshtoken',
            method:'post',
            data:{
                username:adminInfoStore.adminInfo.username,
                refreshToken:adminInfoStore.adminInfo.refreshToken
            },
            __isRefreshToken :true,
        })
        resolve(resp.data.code === '0')
    })
    adminPromise.finally(()=>{
        adminPromise = null
    })
    return adminPromise
}

export function adminIsRefreshRequest(config){
    return !!config.__isRefreshToken
}