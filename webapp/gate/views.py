from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
from django.shortcuts import render
import forms
import logic

def index(request):
    '''The homepage'''
    major = minor = {}
    if request.method == 'POST':
        form = forms.URLForm(request.POST)
        if form.is_valid():
            url = form.cleaned_data['url']
            major, minor = logic.tokenize(url)
    else:
        form = forms.URLForm()
    return render(request, 'index.html', {'form': form, 'major': major, 'minor': minor})
