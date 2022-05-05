package com.pangpangyu.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pangpangyu.constant.RedisConstant;
import com.pangpangyu.dao.SetmealDao;
import com.pangpangyu.entity.PageResult;
import com.pangpangyu.entity.QueryPageBean;
import com.pangpangyu.pojo.Setmeal;
import com.pangpangyu.service.SetmealService;
import com.pangpangyu.utils.QiniuUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")//从属性文件读取输出目录的路径
    private String outputpath ;

    //垃圾图片清理
//    private void deleteTrashPic() {
//        //子集必须放在第二个参数位置，否则结果为空
//        Set<String> sdiff = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
//        if(sdiff != null && sdiff.size() > 0){
//            for (String s : sdiff) {
////                System.out.println(s);
//                QiniuUtils.deleteFileFromQiniu(s);
////                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,s);
//            }
//        }
//    }

    private void savePic2Redis(String pic){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
    }

    private void remPic2Redis(String pic){
        jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        PageHelper.startPage(currentPage, pageSize);
        Page<Setmeal> page = setmealDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {
        setmealDao.add(setmeal);
        Integer setmealId = setmeal.getId();
        //设置检查组和检查项的多对多关联关系，操作t_setmeal_checkgroup表
        this.setSetmealAndCheckGroup(setmealId,checkgroupIds);
        //将图片名称保存到Redis集合中
        savePic2Redis(setmeal.getImg());
        //比对两个集合差异，将垃圾图片删除
//        deleteTrashPic();

        //新增套餐后需要重新生成静态页面
        generateMobileStaticHtml();
    }

    //生成静态页面
    public void generateMobileStaticHtml() {
        //准备模板文件中所需的数据
        List<Setmeal> setmealList = setmealDao.findAll();
        //生成套餐列表静态页面
        generateMobileSetmealListHtml(setmealList);
        //生成套餐详情静态页面（多个）
        generateMobileSetmealDetailHtml(setmealList);
    }

    //生成套餐列表静态页面
    public void generateMobileSetmealListHtml(List<Setmeal> setmealList) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("setmealList", setmealList);
        this.generateHtml("mobile_setmeal.ftl","m_setmeal.html",dataMap);
    }

    //生成套餐详情静态页面（多个）
    public void generateMobileSetmealDetailHtml(List<Setmeal> setmealList) {
        for (Setmeal setmeal : setmealList) {
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("setmeal", setmealDao.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl",
                    "setmeal_detail_"+setmeal.getId()+".html",
                    dataMap);
        }
    }

    private void generateHtml(String templateName,String htmlPageName,Map<String, Object> dataMap) {
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Writer out = null;
            try {
                // 加载模版文件
                Template template = configuration.getTemplate(templateName);
                // 生成数据
                File docFile = new File(outputpath + "\\" + htmlPageName);
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
                // 输出文件
                template.process(dataMap, out);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != out) {
                        out.flush();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
    }

    @Override
    public void edit(Setmeal setmeal, Integer[] checkgroupIds) {
        //清理当前套餐关联的检查组，操作中间关系表
        setmealDao.deleteAssociation(setmeal.getId());
        //重新建立关联关系
        Integer setmealId = setmeal.getId();
        this.setSetmealAndCheckGroup(setmealId,checkgroupIds);
        //修改检查表基本信息，操作检查组表
        setmealDao.edit(setmeal);
//        System.out.println(setmeal.getImg()+"修改后");
        //将修改后的图片保存到数据库
        savePic2Redis(setmeal.getImg());
        //清理垃圾图片
//        deleteTrashPic();
        generateMobileStaticHtml();

    }

    @Override
    public Setmeal findById(Integer id) {
        Setmeal setmeal = setmealDao.findById(id);
//        System.out.println(setmeal.getImg()+"修改前");
        remPic2Redis(setmeal.getImg());
        return setmeal;
    }

    @Override
    public List<Integer> findCheckGroupIdsBySetmealId(Integer id) {
        return setmealDao.findCheckGroupIdsBySetmealId(id);
    }

    @Override
    public void deleteById(Integer id) {
        //先删除中间表关联关系
        setmealDao.deleteAssociation(id);
        //删除垃圾图片
        Setmeal setmeal = setmealDao.findById(id);
//        jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());
        remPic2Redis(setmeal.getImg());
//        QiniuUtils.deleteFileFromQiniu(setmeal.getImg());
        //根据Id删除套餐
        setmealDao.deleteById(id);
    }

    @Override
    public List<Setmeal> findAll() {
        return setmealDao.findAll();
    }

    @Override
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }


    //向中间表(t_checkgroup_checkitem)插入数据（建立检查组和检查项关联关系）
    public void setSetmealAndCheckGroup(Integer setmealId, Integer[] checkgroupIds){
        if (checkgroupIds != null && checkgroupIds.length > 0) {
            Map<String, Integer> map = new HashMap<>();
            for (Integer checkgroupId : checkgroupIds) {
                map.put("setmeal_id",setmealId);
                map.put("checkgroup_id",checkgroupId);
                setmealDao.setCheckGroupAndCheckItem(map);
            }
        }
    }
}
