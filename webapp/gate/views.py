import json
from django.core.urlresolvers import reverse
from django.http import HttpResponse, HttpResponseRedirect
from django.shortcuts import render
from django.utils.safestring import mark_safe
import forms
import logic


def index(request):
    '''The homepage'''
    plots = {}
    if request.method == 'POST':
        form = forms.URLForm(request.POST)
        if form.is_valid():
            url = form.cleaned_data['url']
            major, minor = logic.tokenize(url)
            plots['major'] = mark_safe(logic.flot_data(major))
            for key in minor.keys():
                plots[key] = mark_safe(logic.flot_data(minor[key], major[key]))
    else:
        form = forms.URLForm()
    return render(request, 'index.html', {'form': form, 'plots': plots})


def api(request, url):
    '''Simple API'''
    major, minor = logic.tokenize(url)
    return HttpResponse(json.dumps({'major': major, 'minor': minor}),
                        content_type='application/json; charset=UTF-8')
