require 'net/http'
class HomeController < ApplicationController
  def index
  	if params[:query].nil? || params[:query]==""
  		@docs = JSON.parse("{}")
  	else
  		response = Net::HTTP.get_response(URI.parse(URI.encode("http://hadoop-2:8983/solr/collection1/select?q=content:"<<params[:query]<<"&rows=100&wt=json&indent=true")))
      body = response.body
      body_utf8 = body.force_encoding("UTF-8")
      @docs = JSON.parse(body_utf8)["response"]["docs"]
    end
  end
end
