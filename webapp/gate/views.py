from django.core.urlresolvers import reverse
from django.http import HttpResponseRedirect
from django.shortcuts import render
import forms

def index(request):
    '''The homepage'''
    url = ''
    if request.method == 'POST':
        form = forms.URLForm(request.POST)
        if form.is_valid():
            url = form.cleaned_data['url']
    else:
        form = forms.URLForm()
    return render(request, 'index.html', {'form': form, 'url': url})
