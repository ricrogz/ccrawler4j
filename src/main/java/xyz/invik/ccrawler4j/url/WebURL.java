/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.invik.ccrawler4j.url;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/**
 * @author Yasser Ganjisaffar
 */

@Entity
public class WebURL implements Serializable {

    private static final long serialVersionUID = 1L;

    @PrimaryKey
    private String url;
    private int domainStartIdx;

    private int docid;
    private int parentDocid;
    private String parentUrl;
    private short depth;
    private String domain;
    private String subDomain;
    private String path;
    private String anchor;
    private byte priority;
    private String tag;
    private String label;
    private int redirectionDepth = 0;

    private Map<String, String> urlParameters = new LinkedHashMap<>();
    private Map<String, String> attributes;

    /**
     * @return unique document id assigned to this Url.
     */
    public int getDocid() {
        return docid;
    }

    public void setDocid(int docid) {
        this.docid = docid;
    }

    /**
     * @return Url string
     */
    public String getURL() {
        return getURL(true);
    }

    public String getURL(boolean includeProtocol) {
        if (includeProtocol) {
            return url;
        } else {
            return url.substring(domainStartIdx);
        }
    }

    public void setURL(String url) {
        this.url = url;

        domainStartIdx = url.indexOf("//") + 2;
        int domainEndIdx = url.indexOf('/', domainStartIdx);
        domainEndIdx = (domainEndIdx > domainStartIdx) ? domainEndIdx : url.length();
        domain = url.substring(domainStartIdx, domainEndIdx);
        subDomain = "";
        String[] parts = domain.split("\\.");
        if (parts.length > 2) {
            domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
            int limit = 2;
            if (TLDList.getInstance().contains(domain)) {
                domain = parts[parts.length - 3] + "." + domain;
                limit = 3;
            }
            for (int i = 0; i < (parts.length - limit); i++) {
                if (!subDomain.isEmpty()) {
                    subDomain += ".";
                }
                subDomain += parts[i];
            }
        }
        path = url.substring(domainEndIdx);

        // Check if url includes GET urlParameters
        int pathEndIdx = path.indexOf('?');
        if (pathEndIdx >= 0) {

            // If the path extends past '?', then we have urlParameters, and we parse them
            if (path.length() > pathEndIdx) {
                int idx;
                String args = path.substring(pathEndIdx + 1); // Exclude "?" sign
                String[] pairs = args.split("&");
                String key;
                String value;
                for (String pair : pairs) {
                    idx = pair.indexOf("=");
                    try {
                        if (idx < 0) {
                            key = URLDecoder.decode(pair, "UTF-8");
                            value = "";
                        } else {
                            key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                            value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
                        }
                        urlParameters.put(key, value);
                    } catch (UnsupportedEncodingException e) {
                        System.out.println("ERROR: Unable to decode GET parameter: " + pair);
                    }
                }
            }

            // Redefine path to exclude arguments
            path = path.substring(0, pathEndIdx);
        }
    }

    /**
     * @return
     *      unique document id of the parent page. The parent page is the
     *      page in which the Url of this page is first observed.
     */
    public int getParentDocid() {
        return parentDocid;
    }

    public void setParentDocid(int parentDocid) {
        this.parentDocid = parentDocid;
    }

    /**
     * @return
     *      url of the parent page. The parent page is the page in which
     *      the Url of this page is first observed.
     */
    public String getParentUrl() {
        return parentUrl;
    }

    public void setParentUrl(String parentUrl) {
        this.parentUrl = parentUrl;
    }

    /**
     * @return
     *      crawl depth at which this Url is first observed. Seed Urls
     *      are at depth 0. Urls that are extracted from seed Urls are at depth 1, etc.
     */
    public short getDepth() {
        return depth;
    }

    public void setDepth(short depth) {
        this.depth = depth;
    }

    /**
     * @return
     *      domain of this Url. For 'http://www.example.com/sample.htm', domain will be 'example
     *      .com'
     */
    public String getDomain() {
        return domain;
    }

    public String getSubDomain() {
        return subDomain;
    }

    /**
     * @return
     *      path of this Url. For 'http://www.example.com/sample.htm', domain will be 'sample.htm'
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return
     *      anchor string. For example, in <a href="example.com">A sample anchor</a>
     *      the anchor string is 'A sample anchor'
     */
    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    /**
     * @return priority for crawling this URL. A lower number results in higher priority.
     */
    public byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    /**
     * @return tag in which this URL is found
     * */
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return label applied to the URL
     * */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return number of redirections followed up to this point
     */
    public int getRedirectionDepth() {
        return redirectionDepth;
    }

    public void setRedirectionDepth(int depth) {
        redirectionDepth = depth;
    }

    /**
     * @return Provide access to urlParameters mapping
     * */
    public boolean hasUrlParameter(String name) {
        return urlParameters.containsKey(name);
    }

    public String getUrlParameter(String name) {
        return urlParameters.get(name);
    }

    /**
     * @return Provide access to attributes mapping
     * */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getAttribute(String name) {
        if (attributes == null) {
            return "";
        }
        return attributes.getOrDefault(name, "");
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        WebURL otherUrl = (WebURL) o;
        return (url != null) && url.equals(otherUrl.getURL(false));

    }

    @Override
    public String toString() {
        return url;
    }
}