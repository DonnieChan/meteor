package com.duowan.meteor.model.view.export;

import org.apache.commons.lang.StringUtils;

import com.duowan.meteor.model.enumtype.FileType;
import com.duowan.meteor.model.view.AbstractTaskDepend;

/**
 * Created by chenwu on 2015/7/8 0008.
 */
public class ExportKafkaTask extends AbstractTaskDepend {

	private static final long serialVersionUID = 1635773952522238779L;
	public final String CLASS = "com.duowan.meteor.model.view.export.ExportKafkaTask";
	
	private String toBrokers;
    private String toTopic;   
    private String fetchSql;

    public ExportKafkaTask() {
        this.setFileType(FileType.ExportKafka.name());
        this.setProgramClass("com.duowan.meteor.server.executor.ExportKafkaTaskExecutor");
    }
    
    @Override
	public void doAssert() {
		super.doAssert();
		assertTrue(StringUtils.isNotBlank(this.toBrokers), "toBrokers cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.toTopic), "toTopic cannot be blank!");
		assertTrue(StringUtils.isNotBlank(this.fetchSql), "fetchSql cannot be blank!");
	}
    
    @Override
	public void doTrim() {
		super.doTrim();
		toBrokers = StringUtils.trim(toBrokers);
		toTopic = StringUtils.trim(toTopic);
		fetchSql = StringUtils.trim(fetchSql);
	}

	public String getToBrokers() {
		return toBrokers;
	}

	public void setToBrokers(String toBrokers) {
		this.toBrokers = toBrokers;
	}

	public String getToTopic() {
		return toTopic;
	}

	public void setToTopic(String toTopic) {
		this.toTopic = toTopic;
	}

	public String getFetchSql() {
		return fetchSql;
	}

	public void setFetchSql(String fetchSql) {
		this.fetchSql = fetchSql;
	}

}
