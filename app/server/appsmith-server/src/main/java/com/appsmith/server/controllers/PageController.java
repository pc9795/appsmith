package com.appsmith.server.controllers;

import com.appsmith.server.constants.Url;
import com.appsmith.server.domains.Page;
import com.appsmith.server.dtos.ApplicationPagesDTO;
import com.appsmith.server.dtos.ResponseDTO;
import com.appsmith.server.exceptions.AppsmithError;
import com.appsmith.server.exceptions.AppsmithException;
import com.appsmith.server.services.ApplicationPageService;
import com.appsmith.server.services.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(Url.PAGE_URL)
@Slf4j
public class PageController extends BaseController<PageService, Page, String> {
    private final ApplicationPageService applicationPageService;

    @Autowired
    public PageController(PageService service, ApplicationPageService applicationPageService) {
        super(service);
        this.applicationPageService = applicationPageService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseDTO<Page>> create(@Valid @RequestBody Page resource,
                                          @RequestHeader(name = "Origin", required = false) String originHeader,
                                          ServerWebExchange exchange) {
        log.debug("Going to create resource {}", resource.getClass().getName());
        return applicationPageService.createPage(resource)
                .map(created -> new ResponseDTO<>(HttpStatus.CREATED.value(), created, null));
    }

    @Override
    public Mono<ResponseDTO<List<Page>>> getAll(@RequestParam MultiValueMap<String, String> params) {
        return Mono.error(new AppsmithException(AppsmithError.UNSUPPORTED_OPERATION));
    }

    @Deprecated
    @GetMapping("/application/{applicationId}")
    public Mono<ResponseDTO<ApplicationPagesDTO>> getPageNamesByApplicationId(@PathVariable String applicationId) {
        return service.findNamesByApplicationId(applicationId)
                .map(resources -> new ResponseDTO<>(HttpStatus.OK.value(), resources, null));
    }

    @GetMapping("/application/name/{applicationName}")
    public Mono<ResponseDTO<ApplicationPagesDTO>> getPageNamesByApplicationName(@PathVariable String applicationName) {
        return service.findNamesByApplicationName(applicationName)
                .map(resources -> new ResponseDTO<>(HttpStatus.OK.value(), resources, null));
    }

    @Override
    @GetMapping("/{pageId}")
    public Mono<ResponseDTO<Page>> getById(@PathVariable String pageId) {
        return applicationPageService.getPage(pageId, false)
                .map(page -> new ResponseDTO<>(HttpStatus.OK.value(), page, null));
    }


    @GetMapping("/{pageId}/view")
    public Mono<ResponseDTO<Page>> getPageView(@PathVariable String pageId) {
        return applicationPageService.getPage(pageId, true)
                .map(page -> new ResponseDTO<>(HttpStatus.OK.value(), page, null));
    }

    @GetMapping("{pageName}/application/{applicationName}/view")
    public Mono<ResponseDTO<Page>> getPageViewByName(@PathVariable String applicationName, @PathVariable String pageName) {
        return applicationPageService.getPageByName(applicationName, pageName, true)
                .map(page -> new ResponseDTO<>(HttpStatus.OK.value(), page, null));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseDTO<Page>> delete(@PathVariable String id) {
        log.debug("Going to delete page with id: {}", id);
        return service.delete(id)
                .map(deletedResource -> new ResponseDTO<>(HttpStatus.OK.value(), deletedResource, null));
    }

    @PostMapping("/clone/{pageId}")
    public Mono<ResponseDTO<Page>> clonePage(@PathVariable String pageId) {
        return applicationPageService.clonePage(pageId)
                .map(page -> new ResponseDTO<>(HttpStatus.CREATED.value(), page, null));
    }
}
