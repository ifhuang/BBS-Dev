require 'net/http'
class HomeController < ApplicationController
  def index
  	if params[:query].nil? || params[:query].strip==""
  		@docs = JSON.parse("{}")
  	else
      andquery = ""
      params[:query].split(' ').each do |word|
        andquery<<"%2B%22"<<URI.encode(word)<<"%22"
      end
  		response = Net::HTTP.get_response(URI.parse("http://hadoop-2:8983/solr/collection1/select?q="<<andquery<<"&rows=100&wt=json"))
      body = response.body
      body_utf8 = body.force_encoding("UTF-8")
      @docs = JSON.parse(body_utf8)["response"]["docs"]
    end
  end
end
