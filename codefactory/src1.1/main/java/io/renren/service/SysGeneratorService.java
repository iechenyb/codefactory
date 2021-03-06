package io.renren.service;

import io.renren.dao.SysGeneratorDao;
import io.renren.utils.GenUtils;
import io.renren.utils.RRException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年12月19日 下午3:33:38
 */
@Service
public class SysGeneratorService {
	@Autowired
	private SysGeneratorDao sysGeneratorDao;

	public List<Map<String, Object>> queryList(Map<String, Object> map) {
		return sysGeneratorDao.queryList(map);
	}

	public int queryTotal(Map<String, Object> map) {
		return sysGeneratorDao.queryTotal(map);
	}

	public Map<String, String> queryTable(String tableName) {
		return sysGeneratorDao.queryTable(tableName);
	}

	public List<Map<String, String>> queryColumns(String tableName) {
		return sysGeneratorDao.queryColumns(tableName);
	}

	  /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("table-module-map.properties" );
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }
	public byte[] generatorCode(String[] tableNames) {
		Map<String,String> tableMoudleMap=new HashMap<String, String>();
		Configuration config= getConfig();//获取权限模块
		tableMoudleMap.put("test_table", "test");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(outputStream);

		for(String tableName : tableNames){
			//查询表信息
			Map<String, String> table = queryTable(tableName);
			table.put("moduleName", tableMoudleMap.get(table.get("tableName")));
			//查询列信息
			List<Map<String, String>> columns = queryColumns(tableName);
			//生成代码
			GenUtils.generatorCode(table, columns, zip);
		}
		IOUtils.closeQuietly(zip);
		return outputStream.toByteArray();
	}
}
