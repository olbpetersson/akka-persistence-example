package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

import javax.inject.Singleton;

@Singleton
public class Application extends Controller {

  public Result index() {
    return ok(index.render("Let us Play!"));
  }
}



